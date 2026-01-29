package com.punto_venta.services;

import com.punto_venta.persistance.entities.Users;
import com.punto_venta.web.dtos.request.CreateUserRequest;
import com.punto_venta.web.dtos.request.UpdateUserRequest;
import com.punto_venta.web.dtos.response.BaseResponse;
import jakarta.validation.Valid;

import java.util.Optional;

public interface IUserService {
    BaseResponse createUser(@Valid CreateUserRequest request);
    BaseResponse getUserById (Long id);
    BaseResponse getAllUser ();
    BaseResponse getUserByEmail(String email);
    BaseResponse updateUser(Long id, UpdateUserRequest request);
    BaseResponse deleteUser(Long id);

    Optional<Users> getOptionalUserEmail(String email);
}