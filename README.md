# Triển khai K-means MapReduce
Trong nghiên cứu này, thuật toán phân cụm k-means được triển khai sử dụng framework MapReduce .

## Cài đặt và Sử dụng

### Yêu cầu
- Python 3.x
- Hadoop hoặc một framework MapReduce tương tự
- Các thư viện cần thiết: `numpy`, `pillow`, `matplotlib`

### Cài đặt
Clone repository:
```bash
git clone https://github.com/hoanghelloworld/Kmean-MapReduce-Bigdata.git
cd Kmean-MapReduce-Bigdata
```

Cài đặt các phụ thuộc:
```bash
pip install -r requirements.txt
```

### Cấu hình
Cài đặt các tham số trong file `run.sh`:
- JAR_PATH: đường dẫn đến file jar đã build
- MAIN_CLASS=Main
- INPUT_FILE_PATH: đường dẫn đến file điểm đầu vào
- STATE_PATH: đường dẫn đến file cluster
- NUMBER_OF_REDUCERS=3
- OUTPUT_DIR: đường dẫn thư mục output
- DELTA: ngưỡng dừng của thuật toán
- MAX_ITERATIONS: số vòng lặp tối đa
- DISTANCE: loại khoảng cách sử dụng

### Chạy mã nguồn
1. **Tiền xử lý ảnh:**
   Chuyển đổi ảnh thành biểu diễn pixel hoặc đặc trưng để xử lý.
   ```bash
   python data_prep/data_prep.py --src_img /path/to/images --dst_folder /path/to/output --k_init_centriods 10
   ```

2. **Chạy MapReduce:**
   Thực thi thuật toán KMeans với MapReduce.
   ```bash
   bash run.sh
   ```
3. **Visualizer:**
     python data_prep/visualize_results.py --clusters_path "...\resources\Input\test_2_clusters.txt" --src_img  "...\data_prep\images_test\test_2.jpg" --dst_img "...\data_prep\visual_test\image2_test.jpg"
**Chi tiết script chạy trong file bigdata.txt"
## Quy trình làm việc
Hình dưới đây biểu thị một lần lặp của chương trình MapReduce.

![][asset/11227_2014_1225_Fig1_HTML.png]

Đầu tiên, Centroid và Context (Configuration) được tải vào Distributed Cache. Điều này được thực hiện bằng cách ghi đè hàm setup trong lớp Mapper và Reducer. Sau đó, file dữ liệu đầu vào được chia nhỏ và mỗi điểm dữ liệu được xử lý bởi một trong các hàm map (trong quá trình Map). Hàm ghi các cặp key-value <Centroid, Point>, trong đó Centroid là điểm gần nhất với Point. Tiếp theo, Combiner được sử dụng để giảm số lượng ghi cục bộ. Trong giai đoạn này, các điểm dữ liệu trên cùng một máy được tổng hợp và số lượng các điểm dữ liệu đó được ghi lại, biến Point.number. Bây giờ, vì lý do tối ưu hóa, các giá trị đầu ra được tự động xáo trộn và sắp xếp theo Centroid. Reducer thực hiện cùng một quy trình như Combiner, nhưng nó cũng kiểm tra xem các centroid có hội tụ hay không; so sánh sự khác biệt giữa các centroid cũ và mới với tham số đầu vào delta. Nếu một centroid hội tụ, thì Counter toàn cục không thay đổi, ngược lại, nó được tăng lên.

Sau khi một lần lặp hoàn tất, các centroid mới được lưu và chương trình kiểm tra hai điều kiện, nếu chương trình đạt đến số lần lặp tối đa hoặc nếu giá trị Counter không thay đổi. Nếu một trong hai điều kiện này được thỏa mãn, thì chương trình kết thúc, nếu không, toàn bộ quá trình MapReduce được chạy lại với các centroid đã cập nhật.

## Ví dụ
Một trong những trường hợp sử dụng của thuật toán k-means là quá trình lượng tử hóa màu, giảm số lượng màu sắc khác biệt của hình ảnh. (Có sẵn các thuật toán tốt hơn nhiều cho mục đích này)

Các giá trị số (RGB) của hình ảnh (Hình 1) được lưu làm dữ liệu đầu vào (Hình 2) và các cụm được khởi tạo ngẫu nhiên.

### Hình ảnh gốc

### Hình ảnh sau khi phân cụm

### Hình ảnh đã sửa đổi cho số lượng cluster khác nhau

Input Image                |  K = 9
:-------------------------:|:-------------------------:
![](asset/image.jpg)  |  ![](asset/generated9.jpg)

K = 8                      |  K = 7
:-------------------------:|:-------------------------:
![](generated8.jpg)  |  ![](asset/generated7.jpg)

K = 6                      |  K = 5
:-------------------------:|:-------------------------:
![](generated6.jpg)  |  ![](asset/generated5.jpg)

K = 4                      |  K = 3
:-------------------------:|:-------------------------:
![](asset/generated4.jpg)  |  ![](asset/generated3.jpg)



## Danh sách thành viên

<table>
<tr>

  <td  valign="top" width="14.28%"><a href="https://github.com/hoanghelloworld"><img src="https://avatars.githubusercontent.com/u/115699781?s=96&v=4" width="100px;" /><br /><sub><b>Nguyễn Huy Hoàng</b></sub></a><br/></td>

  <td  valign="top" width="14.28%"><a href="https://github.com/thelong9"><img src="https://avatars.githubusercontent.com/u/125560117?v=4" width="100px;" /><br /><sub><b>Bùi Thế Long</b></sub></a><br/></td>

  <td  valign="top" width="14.28%"><a href="https://github.com/Dyio147"><img src="https://avatars.githubusercontent.com/u/125756779?v=4" width="100px;" /><br /><sub><b>Trần Văn Dy </b></sub></a><br/></td>

  <td  valign="top" width="14.28%"><a href="https://github.com/HienNguyenPhan"><img src="https://avatars.githubusercontent.com/u/120093175?v=4" width="100px;" /><br /><sub><b>Nguyễn Phan Hiển</b></sub></a><br/></td>

</tr>
</table>
