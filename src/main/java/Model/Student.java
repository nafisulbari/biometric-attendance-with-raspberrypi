package Model;

public class Student {
    private int id;
    private String stdId;

    public Student() {
    }

    public Student(int id, String stdId) {
        this.id = id;
        this.stdId = stdId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStdId() {
        return stdId;
    }

    public void setStdId(String stdId) {
        this.stdId = stdId;
    }


    @Override
    public String toString() {
        return "Student{" +
                "id=" + id +
                ", stdId='" + stdId + '\'' +
                '}';
    }
}
