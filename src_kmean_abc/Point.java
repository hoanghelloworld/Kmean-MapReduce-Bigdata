import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.io.Writable;

public class Point implements Writable {
    private List<Double> coordinates;

    public Point() {
        coordinates = new ArrayList<>();
    }

    public Point(String line) {
        coordinates = new ArrayList<>();
        String[] values = line.split("\\s+"); // Thay đổi từ dấu phẩy sang khoảng trắng
        for (String value : values) {
            coordinates.add(Double.parseDouble(value));
        }
    }

    public Point(List<Double> coordinates) {
        this.coordinates = coordinates;
    }

    public List<Double> getCoordinates() {
        return coordinates;
    }

    public double calculateDistance(Point other) {
        double sum = 0.0;
        for (int i = 0; i < coordinates.size(); i++) {
            sum += Math.pow(coordinates.get(i) - other.coordinates.get(i), 2);
        }
        return Math.sqrt(sum);
    }

    @Override
    public void readFields(DataInput in) throws IOException {
        int size = in.readInt();
        coordinates.clear();
        for (int i = 0; i < size; i++) {
            coordinates.add(in.readDouble());
        }
    }

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeInt(coordinates.size());
        for (double coordinate : coordinates) {
            out.writeDouble(coordinate);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < coordinates.size(); i++) {
            sb.append(coordinates.get(i));
            if (i < coordinates.size() - 1) {
                sb.append(" "); // Thay đổi từ dấu phẩy sang khoảng trắng
            }
        }
        return sb.toString();
    }

    public static Point centroid(List<Point> points) {
        if (points.isEmpty()) {
            return null;
        }

        int dimension = points.get(0).getCoordinates().size();
        List<Double> centroidCoordinates = new ArrayList<>(dimension);
        for (int i = 0; i < dimension; i++) {
            centroidCoordinates.add(0.0);
        }

        for (Point point : points) {
            for (int i = 0; i < dimension; i++) {
                centroidCoordinates.set(i, centroidCoordinates.get(i) + point.getCoordinates().get(i));
            }
        }

        for (int i = 0; i < dimension; i++) {
            centroidCoordinates.set(i, centroidCoordinates.get(i) / points.size());
        }

        return new Point(centroidCoordinates);
    }
}