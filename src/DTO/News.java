package DTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class News {
    private Long id;
    private Long categoryId;
    private String name;
    private String publishingHouse;
}
