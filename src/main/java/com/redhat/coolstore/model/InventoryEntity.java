@QuarkusEntity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InventoryEntity implements Serializable {

    @Id
    private String itemId;

    private String location;

    private int quantity;

    private String link;

    public InventoryEntity() {

    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    @Override
    public String toString() {
        return "InventoryEntity [itemId=" + itemId + ", location=" + location + ", quantity=" + quantity + ", link=" + link + "]";
    }
}
