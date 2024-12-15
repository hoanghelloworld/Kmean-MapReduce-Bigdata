import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class KMeansMapper extends Mapper<LongWritable, Text, IntWritable, Point> {
    private List<Point> centroids = new ArrayList<>();

    @Override
    protected void setup(Context context) throws IOException, InterruptedException {
        Configuration conf = context.getConfiguration();
        Path centroidsPath = new Path(conf.get("centroidsPath"));
        FileSystem fs = FileSystem.get(conf);
        FSDataInputStream in = fs.open(centroidsPath);
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        String line;
        while ((line = br.readLine()) != null) {
            Point centroid = new Point(line);
            centroids.add(centroid);
        }
        br.close();
    }

    @Override
    public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        Point point = new Point(value.toString());
        int nearestCentroidIndex = -1;
        double minDistance = Double.MAX_VALUE;

        for (int i = 0; i < centroids.size(); i++) {
            double distance = point.calculateDistance(centroids.get(i));
            if (distance < minDistance) {
                minDistance = distance;
                nearestCentroidIndex = i;
            }
        }

        context.write(new IntWritable(nearestCentroidIndex), point);
    }
}