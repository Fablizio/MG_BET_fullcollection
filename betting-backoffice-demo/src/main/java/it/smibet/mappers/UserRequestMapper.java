package it.smibet.mappers;

import it.smibet.domain.UserRequest;
import it.smibet.dto.UserRequestDTO;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class UserRequestMapper {

    private static final String PATTERN = "dd/MM/yyyy";

    public List<UserRequest> convertDTOToEntity(List<UserRequestDTO> userRequestDTOList) {
        return userRequestDTOList.stream().map(this::convertDTOToEntity).collect(Collectors.toList());
    }

    public List<UserRequestDTO> convertEntityToDTO(List<UserRequest> userRequestList) {
        return userRequestList.stream().map(this::convertEntityToDTO).collect(Collectors.toList());
    }

    public UserRequest convertDTOToEntity(UserRequestDTO userRequestDTO) {
        if (userRequestDTO == null) {
            return null;
        }

        try {
            UserRequest userRequest = new UserRequest();

            userRequest.setId(userRequestDTO.getId());
            Optional.ofNullable(userRequestDTO.getRequestDate()).ifPresent(date -> userRequest.setRequestDate(LocalDate.parse(date, DateTimeFormatter.ofPattern(PATTERN))));
            userRequest.setRequestType(userRequestDTO.getRequestType());
            userRequest.setPaymentCode(userRequestDTO.getPaymentCode());

            return userRequest;
        } catch (Exception e) {
            throw e;
        }
    }

    public UserRequestDTO convertEntityToDTO(UserRequest userRequest) {
        if (userRequest == null) {
            return null;
        }

        try {
            UserRequestDTO userRequestDTO = new UserRequestDTO();

            userRequestDTO.setId(userRequest.getId());
            Optional.ofNullable(userRequest.getRequestDate()).ifPresent(date -> userRequestDTO.setRequestDate(date.format(DateTimeFormatter.ofPattern(PATTERN))));
            userRequestDTO.setRequestType(userRequest.getRequestType());
            userRequestDTO.setPaymentCode(userRequest.getPaymentCode());


            return userRequestDTO;
        } catch (Exception e) {
            throw e;
        }
    }


}
