package it.smibet.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserListDTO {

    private List<UserDTO> users;
    private Long expiring;
    private Long expirated;
    private Long activedPayed;
    private Long activedTest;

}
