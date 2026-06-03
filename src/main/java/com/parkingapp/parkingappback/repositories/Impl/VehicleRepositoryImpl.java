package com.parkingapp.parkingappback.repositories.Impl;

import com.parkingapp.parkingappback.entities.Owner;
import com.parkingapp.parkingappback.entities.Vehicle;
import com.parkingapp.parkingappback.repositories.VehicleRepository;
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
public class VehicleRepositoryImpl implements VehicleRepository {
  private final JdbcTemplate jdbcTemplate;

  private Vehicle mapVehicles(ResultSet rs) throws SQLException {
    Owner owner = new Owner();
    owner.setId(rs.getObject("owner_id", UUID.class));
    owner.setFullName(rs.getString("full_name"));
    owner.setPhoneNumber(rs.getString("phone_number"));

    Vehicle vehicle = new Vehicle();
    vehicle.setId(rs.getObject("vehicle_id", UUID.class));
    vehicle.setLicensePlate(rs.getString("license_plate"));
    vehicle.setBrand(rs.getString("brand"));
    vehicle.setModel(rs.getString("model"));
    vehicle.setOwner(owner);

    return vehicle;
  }

  @Override
  public List<Vehicle> findAll(){
    String sql = """
      SELECT v.id as vehicle_id, v.license_plate, v.brand, v.model, o.id as owner_id, o.full_name, o.phone_number
      FROM vehicles v
      JOIN owners o ON v.owner_id = o.id
      ORDER BY o.full_name
      """;

    return jdbcTemplate.query(sql, (rs, rowNum) -> mapVehicles(rs));
  }

  @Override
  public Optional<Vehicle> findById(UUID id){
    String sql = """
      SELECT v.id as vehicle_id, v.license_plate, v.brand, v.model, o.id as owner_id, o.full_name, o.phone_number
      FROM vehicles v
      JOIN owners o ON v.owner_id = o.id
      WHERE v.id = ?
      """;
    List<Vehicle> vehicles = jdbcTemplate.query(sql, (rs, rowNum) -> mapVehicles(rs), id);

    return vehicles.isEmpty() ? Optional.empty() : Optional.of(vehicles.getFirst());
  }

  @Override
  public List<Vehicle> findByLicensePlate(String licensePlate){
    String sql = """
      SELECT v.id as vehicle_id, v.license_plate, v.brand, v.model, o.id as owner_id, o.full_name, o.phone_number
      FROM vehicles v
      JOIN owners o ON v.owner_id = o.id
      WHERE LOWER(v.license_plate) LIKE LOWER(?)
      ORDER BY o.full_name
      """;

    return jdbcTemplate.query(sql, (rs, rowNum) -> mapVehicles(rs), "%" + licensePlate + "%");
  }

  @Override
  public Vehicle create(Vehicle vehicle){
    if (vehicle.getId() == null) {
      vehicle.setId(UUID.randomUUID());
    }

    String sql = "INSERT INTO vehicles (id, license_plate, brand, model, owner_id) VALUES (?, ?, ?, ?, ?)";
    jdbcTemplate.update(sql,
      vehicle.getId(), vehicle.getLicensePlate(), vehicle.getBrand(), vehicle.getModel(), vehicle.getOwner().getId());

    return vehicle;
  }

  @Override
  public Vehicle update(Vehicle vehicle){
    String sql = "UPDATE vehicles SET license_plate = ?, brand = ?, model = ?, owner_id = ? WHERE id = ?";
    jdbcTemplate.update(sql,
      vehicle.getLicensePlate(), vehicle.getBrand(), vehicle.getModel(), vehicle.getOwner().getId(), vehicle.getId());

    return vehicle;
  }

  @Override
  public boolean deleteById(UUID id){
    String sql = "DELETE FROM vehicles WHERE id = ?";
    int affectedRowsCount = jdbcTemplate.update(sql, id);

    return affectedRowsCount > 0;
  }

  @Override
  public boolean existsById(UUID id){
    String sql = "SELECT COUNT(*) FROM vehicles WHERE id = ?";
    Integer count = jdbcTemplate.queryForObject(sql, Integer.class, id);

    return count != null && count > 0;
  }

  @Override
  public boolean existsByLicensePlate(String licensePlate){
    String sql = "SELECT COUNT(*) FROM vehicles WHERE license_plate = ?";
    Integer count = jdbcTemplate.queryForObject(sql, Integer.class, licensePlate);

    return count != null && count > 0;
  }
}
