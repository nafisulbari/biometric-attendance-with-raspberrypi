package Model;


import javax.persistence.*;
import java.util.Arrays;

@Entity
@Table(name= "student")
public class DbStudent {

@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private int id;

private String std_Id;

private byte[] fingerPrint;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStd_Id() {
        return std_Id;
    }

    public void setStd_Id(String std_Id) {
        this.std_Id = std_Id;
    }

    public byte[] getFingerPrint() {
        return fingerPrint;
    }

    public void setFingerPrint(byte[] fingerPrint) {
        this.fingerPrint = fingerPrint;
    }


    @Override
    public String toString() {
        return "DbStudent{" +
                "id=" + id +
                ", std_Id='" + std_Id + '\'' +
                ", fingerPrint=" + Arrays.toString(fingerPrint) +
                '}';
    }
}
