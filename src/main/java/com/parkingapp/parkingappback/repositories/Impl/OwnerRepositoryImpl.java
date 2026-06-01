package com.parkingapp.parkingappback.repositories;

import com.parkingapp.parkingappback.entities.Owner;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@AllArgsConstructor
public class OwnerRepository {
  private final JdbcTemplate jdbcTemplate;

  private Owner mapOwners(ResultSet rs) throws SQLException {
    Owner owner = new Owner();
    owner.setId(rs.getObject("id", UUID.class));
    owner.setFullName(rs.getString("full_name"));
    owner.setPhoneNumber(rs.getString("phone_number"));

    return owner;
  }

  public List<Owner> findAll(){
    String sql = "SELECT id, full_name, phone_number FROM owners";

    return jdbcTemplate.query(sql, (rs, rowNum) -> mapOwners(rs));
  }

  public Optional<Owner> findById(UUID id){
    String sql = "SELECT id, full_name, phone_number FROM owners WHERE id = ?";
    List<Owner> owners = jdbcTemplate.query(sql, (rs, rowNum) -> mapOwners(rs), id);

    return owners.isEmpty() ? Optional.empty() : Optional.of(owners.getFirst());
  }

  public List<Owner> findByFullName(String fullName){
    String sql = "SELECT id, full_name, phone_number FROM owners WHERE LOWER(full_name) LIKE LOWER(?)";

    return jdbcTemplate.query(sql, (rs, rowNum) -> mapOwners(rs), "%" + fullName + "%");
  }

  public Owner create(Owner owner){
    if (owner.getId() == null) {
      owner.setId(UUID.randomUUID());
    }

    String sql = "INSERT INTO owners (id, full_name, phone_number) VALUES (?, ?, ?)";
    jdbcTemplate.update(sql,owner.getId(), owner.getFullName(), owner.getPhoneNumber());

    return owner;
  }

  public Owner update(Owner owner){
    String sql = "UPDATE owners SET full_name = ?, phone_number = ? WHERE id = ?";
    jdbcTemplate.update(sql, owner.getFullName(), owner.getPhoneNumber(), owner.getId());

    return owner;
  }

  public boolean deleteById(UUID id){
    String sql = "DELETE FROM owners WHERE id = ?";
    int affectedRowsCount = jdbcTemplate.update(sql, id);

    return affectedRowsCount > 0;
  }

  public boolean existsById(UUID id){
    String sql = "SELECT COUNT(*) FROM owners WHERE id = ?";
    Integer count = jdbcTemplate.queryForObject(sql, Integer.class, id);

    return count != null && count > 0;
  }

  public boolean existsByPhoneNumber(String phoneNumber){
    String sql = "SELECT COUNT(*) FROM owners WHERE phone_number = ?";
    Integer count = jdbcTemplate.queryForObject(sql, Integer.class, phoneNumber);

    return count != null && count > 0;
  }
}
