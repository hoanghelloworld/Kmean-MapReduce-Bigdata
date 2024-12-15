import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class KMeansReducer extends Reducer<IntWritable, Point, IntWritable, Text> {
    private int numIterations;
    private int currentIteration;
    private int k;
    @Override
    protected void setup(Context context) throws IOException, InterruptedException {
        numIterations = context.getConfiguration().getInt("numIterations", 10); // Lấy giá trị mặc định là 10 nếu không được cấu hình
        currentIteration = context.getConfiguration().getInt("currentIteration", 1); // Lấy giá trị mặc định là 1 nếu không được cấu hình
        k = context.getConfiguration().getInt("k", 2);
    }
    @Override
    public void reduce(IntWritable key, Iterable<Point> values, Context context)
            throws IOException, InterruptedException {
        List<Point> points = new ArrayList<>();
        for (Point value : values) {
            points.add(new Point(value.getCoordinates())); // Tạo bản sao của Point
        }

        Point newCentroid = Point.centroid(points);

        // Simple ABC update (only one scout bee)
        if (currentIteration < numIterations) {
            Random random = new Random();
            if (random.nextDouble() < 0.1) { // 10% chance to explore (scout bee)
                int randomIndex = random.nextInt(points.size());
                Point randomPoint = points.get(randomIndex);
              List<Double> newCoordinates = new ArrayList<>();
                for(int i = 0; i < randomPoint.getCoordinates().size(); i++){
                    newCoordinates.add(random.nextDouble()*10);
                }

                newCentroid = new Point(newCoordinates); // Explore new position based on a random point
            }
             for (Point point : points) {
                context.write(key, new Text(point.toString()));
            }
        } else {
             // Ở lần lặp cuối, ghi ra điểm và cụm tương ứng
            for (Point point : points) {
                context.write(key, new Text(point.toString()));
            }
        }

        // Vẫn ghi ra tâm cụm mới (cho các lần lặp tiếp theo)
        context.write(key, new Text(newCentroid.toString()));
    }
}