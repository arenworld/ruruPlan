package net.datasa.ruruplan.member;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "email_token")
public class EmailToken {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name="token")
    private String token;

    @Column(name = "email")
    private String email;

    @Column(name = "expiry_date")
    private LocalDateTime expiryDate;

    // 0은 인증되지 않은 상태, 1은 인증된 상태
    @Column(name = "varified", columnDefinition = "DEFAULT 0 check(varified in(0, 1)")
    private boolean verified;
}
