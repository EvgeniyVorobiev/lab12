package org.example;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OptimisticLock;

@Entity
public class Items
{
    @Getter
    @Setter
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    @Column (name = "Id", nullable = false)
    int id;
    @Getter
    @Setter
    @Column (name = "value", nullable = false)
    int value;

    @Getter
    @Version
    long version;

    public Items(int val, int version)
    {
        this.value = val;
        this.version = version;
    }

    public Items() {
    }
    @Override
    public String toString(){
        return String.format("%d", this.value);
    }
}
