package org.cedac.batchtest.tasklet;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@Component
@StepScope
public class MoveFileTasklet implements Tasklet {

    @Value("${input.file.path}") // Specify the path of the input CSV file
    private String inputFilePath;

    @Value("${output.file.path}") // Specify the path of the destination folder
    private String outputFolderPath;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        File inputFile = new File(inputFilePath);
        File outputFolder = new File(outputFolderPath);

        // Create the output folder if it doesn't exist
        if (!outputFolder.exists()) {
            outputFolder.mkdirs();
        }

        // Move the input file to the output folder
        Path source = inputFile.toPath();
        Path destination = outputFolder.toPath().resolve(inputFile.getName());
        Files.move(source, destination, StandardCopyOption.REPLACE_EXISTING);

        return RepeatStatus.FINISHED;
    }
}
