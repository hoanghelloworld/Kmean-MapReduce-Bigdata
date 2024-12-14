# Triển khai K-means MapReduce
Trong nghiên cứu này, thuật toán phân cụm k-means được triển khai sử dụng framework MapReduce (Hadoop phiên bản 2.8).

Để chạy chương trình, shell script `run.sh` cần được thực thi. Nó yêu cầu đường dẫn đến file jar và các tham số đầu vào là:

* `input` - đường dẫn đến file dữ liệu
* `state` - đường dẫn đến file chứa các cụm
* `number` - số lượng reducer
* `output` - thư mục đầu ra
* `delta` - ngưỡng hội tụ (sự khác biệt chấp nhận được giữa 2 centroid liên tiếp)
* `max` - số lần lặp tối đa
* `distance` - độ đo tương tự (hiện tại chỉ hỗ trợ khoảng cách Euclidean)

## Quy trình làm việc
Hình dưới đây biểu thị một lần lặp của chương trình MapReduce.

![alt text][flow]

Đầu tiên, Centroid và Context (Configuration) được tải vào Distributed Cache. Điều này được thực hiện bằng cách ghi đè hàm setup trong lớp Mapper và Reducer. Sau đó, file dữ liệu đầu vào được chia nhỏ và mỗi điểm dữ liệu được xử lý bởi một trong các hàm map (trong quá trình Map). Hàm ghi các cặp key-value <Centroid, Point>, trong đó Centroid là điểm gần nhất với Point. Tiếp theo, Combiner được sử dụng để giảm số lượng ghi cục bộ. Trong giai đoạn này, các điểm dữ liệu trên cùng một máy được tổng hợp và số lượng các điểm dữ liệu đó được ghi lại, biến Point.number. Bây giờ, vì lý do tối ưu hóa, các giá trị đầu ra được tự động xáo trộn và sắp xếp theo Centroid. Reducer thực hiện cùng một quy trình như Combiner, nhưng nó cũng kiểm tra xem các centroid có hội tụ hay không; so sánh sự khác biệt giữa các centroid cũ và mới với tham số đầu vào delta. Nếu một centroid hội tụ, thì Counter toàn cục không thay đổi, ngược lại, nó được tăng lên.

Sau khi một lần lặp hoàn tất, các centroid mới được lưu và chương trình kiểm tra hai điều kiện, nếu chương trình đạt đến số lần lặp tối đa hoặc nếu giá trị Counter không thay đổi. Nếu một trong hai điều kiện này được thỏa mãn, thì chương trình kết thúc, nếu không, toàn bộ quá trình MapReduce được chạy lại với các centroid đã cập nhật.

## Ví dụ
Một trong những trường hợp sử dụng của thuật toán k-means là quá trình lượng tử hóa màu, giảm số lượng màu sắc khác biệt của hình ảnh. (Có sẵn các thuật toán tốt hơn nhiều cho mục đích này)

Các giá trị số (RGB) của hình ảnh (Hình 1) được lưu làm dữ liệu đầu vào (Hình 2) và các cụm được khởi tạo ngẫu nhiên.

### Hình ảnh gốc

![alt text][fig1]

### Giá trị RGB của hình ảnh gốc và đã sửa đổi

![alt text][fig2]

#### Sau 10 lần lặp với 10 cụm, các giá trị RBG được biểu diễn trong Hình 3. Có thể lưu ý rằng một vài centroid đã biến mất.

![alt text][fig3]

### Hình ảnh đã sửa đổi cho số lượng centroid khác nhau

![alt text][fig4]

### Hình ảnh đã sửa đổi cho số lần lặp khác nhau và 10 centroid

![alt text][fig5]

![alt text][fig6]

[flow]: https://github.com/Maki94/kmeans_mapreduce/blob/master/figures/alg.png "Một lần lặp MapReduce"

[fig1]: https://github.com/Maki94/kmeans_mapreduce/blob/master/figures/fig1.PNG "Hình ảnh gốc"
[fig2]: https://github.com/Maki94/kmeans_mapreduce/blob/master/figures/fig2.PNG "Mô hình RGB"
[fig3]: https://github.com/Maki94/kmeans_mapreduce/blob/master/figures/fig3.PNG "Lần lặp thứ 10, 10 cụm"
[fig4]: https://github.com/Maki94/kmeans_mapreduce/blob/master/figures/fig4.PNG "Số lượng cụm khác nhau, lần lặp thứ 10"
[fig5]: https://github.com/Maki94/kmeans_mapreduce/blob/master/figures/fig5.PNG "Số lần lặp khác nhau, 10 cụm"
[fig6]: https://github.com/Maki94/kmeans_mapreduce/blob/master/figures/fig6.PNG "Số lần lặp khác nhau, 10 cụm"
## Danh sách thành viên

<table>
<tr>

  <td  valign="top" width="14.28%"><a href="https://github.com/hoanghelloworld"><img src="https://avatars.githubusercontent.com/u/115699781?s=96&v=4" width="100px;" /><br /><sub><b>Nguyễn Huy Hoàng</b></sub></a><br/></td>

  <td  valign="top" width="14.28%"><a href="https://github.com/thelong9"><img src="https://avatars.githubusercontent.com/u/125560117?v=4" width="100px;" /><br /><sub><b>Bùi Thế Long</b></sub></a><br/></td>

  <td  valign="top" width="14.28%"><a href="https://github.com/Dyio147"><img src="https://avatars.githubusercontent.com/u/125756779?v=4" width="100px;" /><br /><sub><b>Trần Văn Dy </b></sub></a><br/></td>

  <td  valign="top" width="14.28%"><a href="https://github.com/HienNguyenPhan"><img src="https://avatars.githubusercontent.com/u/120093175?v=4" width="100px;" /><br /><sub><b>Nguyễn Phan Hiển</b></sub></a><br/></td>

</tr>
</table>
