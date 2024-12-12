package proj.teachers_proj;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "rates")
public class Rate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private int value;

    @ManyToOne(optional = false)
    @JoinColumn(name = "group_id", nullable = false)
    private ClassTeacher group;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private String comment;

    public Rate() {
    }

    public Rate(int value, ClassTeacher group, LocalDate date, String comment) {
        this.value = value;
        this.group = group;
        this.date = date;
        this.comment = comment;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        if (value < 0 || value > 6) {
            throw new IllegalArgumentException("Value must be between 0 and 6.");
        }
        this.value = value;
    }

    public ClassTeacher getGroup() {
        return group;
    }

    public void setGroup(ClassTeacher group) {
        this.group = group;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        if (comment == null || comment.trim().isEmpty()) {
            throw new IllegalArgumentException("Comment cannot be null or empty.");
        }
        this.comment = comment;
    }

    @Override
    public String toString() {
        return "Ocena: " + value + " (" + comment + ") - Data: " + date;
    }

}
