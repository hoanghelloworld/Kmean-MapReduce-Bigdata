import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

public class KMeansDriver {
    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        GenericOptionsParser optionParser = new GenericOptionsParser(conf, args);
        String[] remainingArgs = optionParser.getRemainingArgs();

        Path inputPath = null;
        Path centroidsPath = null;
        Path outputPath = null;
        int numIterations = 10; // Giá trị mặc định
        int k = 0; // Giá trị mặc định

        // Parse command line arguments
        for (int i = 0; i < remainingArgs.length; ++i) {
            if ("--input".equals(remainingArgs[i])) {
                inputPath = new Path(remainingArgs[++i]);
            } else if ("--state".equals(remainingArgs[i])) {
                centroidsPath = new Path(remainingArgs[++i]);
            } else if ("--output".equals(remainingArgs[i])) {
                outputPath = new Path(remainingArgs[++i]);
            } else if ("--max".equals(remainingArgs[i])) {
                numIterations = Integer.parseInt(remainingArgs[++i]);
            } else if ("--k".equals(remainingArgs[i])) {
               k = Integer.parseInt(remainingArgs[++i]);
            }
        }

        if (inputPath == null || centroidsPath == null || outputPath == null || k == 0) {
            System.err.println("Usage: KMeansDriver --input <input> --state <centroids> --output <output> --max <numIterations> --k <k>");
            System.exit(2);
        }

        conf.setInt("numIterations", numIterations);
        conf.setInt("k",k);
        FileSystem fs = FileSystem.get(conf);
        if (!fs.exists(centroidsPath)) {
            System.err.println("Error: Initial centroids file does not exist.");
            System.exit(2);
        }

        Path tempCentroids = new Path(outputPath.getParent(), "temp_centroids");
        if (!fs.exists(tempCentroids)) {
            fs.mkdirs(tempCentroids);
        }

        for (int iteration = 1; iteration <= numIterations; iteration++) {
            conf.setInt("currentIteration", iteration);
            conf.set("centroidsPath", centroidsPath.toString());

            Job job = Job.getInstance(conf, "K-Means Iteration " + iteration);
            job.setJarByClass(KMeansDriver.class);
            job.setMapperClass(KMeansMapper.class);
            job.setReducerClass(KMeansReducer.class);
            job.setMapOutputKeyClass(IntWritable.class);
            job.setMapOutputValueClass(Point.class);
            job.setOutputKeyClass(IntWritable.class);
            job.setOutputValueClass(Text.class);

            if (iteration == 1) {
                FileInputFormat.addInputPath(job, inputPath);
            } else {
                FileInputFormat.addInputPath(job, new Path(outputPath, "iteration_" + (iteration - 1)));
            }

            Path iterationOutputPath = new Path(outputPath, "iteration_" + iteration);
            FileOutputFormat.setOutputPath(job, iterationOutputPath);

            job.waitForCompletion(true);

            centroidsPath = new Path(tempCentroids, "centroids_iteration_" + iteration);
            fs.delete(centroidsPath, true);
            fs.rename(new Path(iterationOutputPath, "part-r-00000"), centroidsPath);
        }

        // Đổi tên thư mục đầu ra cuối cùng
        Path finalOutputPath = new Path(outputPath.getParent(), "outputcluster.txt");
        fs.rename(new Path(outputPath, "iteration_" + numIterations), finalOutputPath);

        // Xóa thư mục temp_centroids và các thư mục iteration trung gian
        fs.delete(tempCentroids, true);
        for (int i = 1; i < numIterations; i++) {
            fs.delete(new Path(outputPath, "iteration_" + i), true);
        }
        //xóa output
         fs.delete(outputPath, true);
    }
}