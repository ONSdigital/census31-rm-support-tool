package uk.gov.ons.census.supporttool.schedule;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.ons.census.common.model.entity.*;
import uk.gov.ons.census.supporttool.model.repository.JobRepository;
import uk.gov.ons.census.supporttool.model.repository.JobRowRepository;

@ExtendWith(MockitoExtension.class)
public class RowChunkStagerTest {
  @Mock private JobRepository jobRepository;

  @Mock private JobRowRepository jobRowRepository;

  @Captor private ArgumentCaptor<List<JobRow>> jobRowCaptor;

  @Captor private ArgumentCaptor<Job> jobCaptor;

  @Mock CSVReader csvReader;

  @InjectMocks RowChunkStager underTest;

  @Test
  void testRowChunkStagerFailedWithNullChunk() throws IOException, CsvValidationException {
    Job job = getJob();

    String[] headerRow = {"a", "b", "c"};

    when(csvReader.readNext()).thenReturn(null);

    JobStatus jobStatus = underTest.stageChunk(job, headerRow, csvReader, 1);

    assertThat(jobStatus).isEqualTo(JobStatus.VALIDATED_TOTAL_FAILURE);
    verify(csvReader).readNext();
    verify(jobRepository).saveAndFlush(jobCaptor.capture());
    Job capturedJob = jobCaptor.getValue();
    assertThat(capturedJob.getFatalErrorDescription())
        .isEqualTo(
            "Failed to process job due to an empty chunk, this probably indicates a mismatch between file line count and row count");
  }

  @Test
  void testRowChunkStagerSuccess() throws IOException, CsvValidationException {
    Job job = getJob();

    String[] headerRow = {"a", "b", "c"};

    when(csvReader.readNext()).thenReturn(new String[] {"1", "2", "3"}).thenReturn(null);

    JobStatus jobStatus = underTest.stageChunk(job, headerRow, csvReader, 1);

    assertThat(jobStatus).isEqualTo(JobStatus.VALIDATION_IN_PROGRESS);
    verify(csvReader, times(2)).readNext();
    verify(jobRepository).saveAndFlush(jobCaptor.capture());

    Job capturedJob = jobCaptor.getValue();
    assertThat(capturedJob.getStagingRowNumber()).isEqualTo(1);

    verify(jobRowRepository).saveAll(jobRowCaptor.capture());
    List<JobRow> jobRow = jobRowCaptor.getValue();

    assertThat(jobRow.get(0).getJob()).isEqualTo(capturedJob);
    assertThat(jobRow.get(0).getRowData()).isEqualTo(Map.of("a", "1", "b", "2", "c", "3"));
    // Verify the fix: file line number should account for header row
    assertThat(jobRow.get(0).getOriginalRowLineNumber())
        .isEqualTo(2); // 1 (stagingRowNumber) + 1 (header offset)
  }

  @Test
  void testRowChunkStagerFailedWithIOException() throws IOException, CsvValidationException {
    Job job = getJob();

    String[] headerRow = {"a", "b", "c"};

    when(csvReader.readNext()).thenThrow(new IOException());

    JobStatus jobStatus = underTest.stageChunk(job, headerRow, csvReader, 1);

    assertThat(jobStatus).isEqualTo(JobStatus.VALIDATED_TOTAL_FAILURE);
    verify(csvReader).readNext();
  }

  @Test
  void testRowChunkStagerMultipleRowsWithCorrectLineNumbers()
      throws IOException, CsvValidationException {
    Job job = getJob();
    job.setStagingRowNumber(0);

    String[] headerRow = {"a", "b", "c"};

    when(csvReader.readNext())
        .thenReturn(new String[] {"1", "2", "3"}) // First data row (stagingRowNumber becomes 1)
        .thenReturn(new String[] {"4", "5", "6"}) // Second data row (stagingRowNumber becomes 2)
        .thenReturn(new String[] {"7", "8", "9"}) // Third data row (stagingRowNumber becomes 3)
        .thenReturn(null);

    JobStatus jobStatus = underTest.stageChunk(job, headerRow, csvReader, 1);

    assertThat(jobStatus).isEqualTo(JobStatus.VALIDATION_IN_PROGRESS);

    verify(jobRowRepository).saveAll(jobRowCaptor.capture());
    List<JobRow> jobRows = jobRowCaptor.getValue();

    // Verify line numbers account for header row
    // File structure: Line 1 = Header, Line 2 = First data row, Line 3 = Second data row, etc.
    assertThat(jobRows.get(0).getOriginalRowLineNumber()).isEqualTo(2); // First data row at line 2
    assertThat(jobRows.get(1).getOriginalRowLineNumber()).isEqualTo(3); // Second data row at line 3
    assertThat(jobRows.get(2).getOriginalRowLineNumber()).isEqualTo(4); // Third data row at line 4
  }

  @Test
  void testRowChunkStagerHeaderlessFileWithCorrectLineNumbers()
      throws IOException, CsvValidationException {
    Job job = getJob();
    job.setStagingRowNumber(0);

    String[] headerRow = {"a", "b", "c"}; // Expected columns (not from file)

    when(csvReader.readNext())
        .thenReturn(new String[] {"1", "2", "3"}) // First data row (stagingRowNumber becomes 1)
        .thenReturn(new String[] {"4", "5", "6"}) // Second data row (stagingRowNumber becomes 2)
        .thenReturn(new String[] {"7", "8", "9"}) // Third data row (stagingRowNumber becomes 3)
        .thenReturn(null);

    // For headerless files, headerRowOffset = 0 (no header to account for)
    JobStatus jobStatus = underTest.stageChunk(job, headerRow, csvReader, 0);

    assertThat(jobStatus).isEqualTo(JobStatus.VALIDATION_IN_PROGRESS);

    verify(jobRowRepository).saveAll(jobRowCaptor.capture());
    List<JobRow> jobRows = jobRowCaptor.getValue();

    // Verify line numbers for headerless file (no offset applied)
    // File structure: Line 1 = First data row, Line 2 = Second data row, etc.
    assertThat(jobRows.get(0).getOriginalRowLineNumber()).isEqualTo(1); // First data row at line 1
    assertThat(jobRows.get(1).getOriginalRowLineNumber()).isEqualTo(2); // Second data row at line 2
    assertThat(jobRows.get(2).getOriginalRowLineNumber()).isEqualTo(3); // Third data row at line 3
  }

  private Job getJob() {
    CollectionExercise collectionExercise = new CollectionExercise();
    Job job = new Job();
    job.setCollectionExercise(collectionExercise);
    job.setJobStatus(JobStatus.FILE_UPLOADED);
    job.setJobType(JobType.SAMPLE);
    job.setFileRowCount(1);
    job.setId(UUID.randomUUID());
    job.setFileId(UUID.randomUUID());
    return job;
  }
}
