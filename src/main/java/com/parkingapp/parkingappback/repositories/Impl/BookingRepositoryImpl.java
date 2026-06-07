package com.parkingapp.parkingappback.repositories.Impl;

import com.parkingapp.parkingappback.entities.Booking;
import com.parkingapp.parkingappback.entities.Owner;
import com.parkingapp.parkingappback.entities.ParkingSpot;
import com.parkingapp.parkingappback.entities.Vehicle;
import com.parkingapp.parkingappback.repositories.BookingRepository;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@AllArgsConstructor
public class BookingRepositoryImpl implements BookingRepository {
  private final JdbcTemplate jdbcTemplate;

  private Booking mapBookings(ResultSet rs) throws SQLException {
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

    ParkingSpot parkingSpot = new ParkingSpot();
    parkingSpot.setId(rs.getObject("parking_spot_id", UUID.class));
    parkingSpot.setSpotNumber(rs.getString("spot_number"));
    parkingSpot.setOccupied(rs.getBoolean("is_occupied"));

    Booking booking = new Booking();
    booking.setId(rs.getObject("booking_id", UUID.class));
    Timestamp startTimestamp = rs.getTimestamp("start_time");
    booking.setStartTime(startTimestamp.toInstant());
    Timestamp endTimestamp = rs.getTimestamp("end_time");
    booking.setEndTime(endTimestamp.toInstant());
    booking.setPaid(rs.getBoolean("is_paid"));
    booking.setCost(rs.getInt("cost"));
    booking.setParkingSpot(parkingSpot);
    booking.setVehicle(vehicle);

    return booking;
  }

  @Override
  public List<Booking> findAll(){
    String sql = """
      SELECT b.id as booking_id, b.start_time, b.end_time, b.is_paid, b.cost,
             ps.id as parking_spot_id, ps.spot_number, ps.is_occupied,
             v.id as vehicle_id, v.license_plate, v.brand, v.model,
             o.id as owner_id, o.full_name, o.phone_number
      FROM bookings b
      JOIN parking_spots ps ON b.parking_spot_id = ps.id
      JOIN vehicles v ON b.vehicle_id = v.id
      JOIN owners o ON v.owner_id = o.id
      ORDER BY o.full_name
      """;

    return jdbcTemplate.query(sql, (rs, rowNum) -> mapBookings(rs));
  }

  @Override
  public Optional<Booking> findById(UUID id){
    String sql = """
      SELECT b.id as booking_id, b.start_time, b.end_time, b.is_paid, b.cost,
             ps.id as parking_spot_id, ps.spot_number, ps.is_occupied,
             v.id as vehicle_id, v.license_plate, v.brand, v.model,
             o.id as owner_id, o.full_name, o.phone_number
      FROM bookings b
      JOIN parking_spots ps ON b.parking_spot_id = ps.id
      JOIN vehicles v ON b.vehicle_id = v.id
      JOIN owners o ON v.owner_id = o.id
      WHERE b.id = ?
      """;
    List<Booking> bookings = jdbcTemplate.query(sql, (rs, rowNum) -> mapBookings(rs), id);

    return bookings.isEmpty() ? Optional.empty() : Optional.of(bookings.getFirst());
  }

  @Override
  public List<Booking> findByVehicleLicensePlate(String licensePlate){
    String sql = """
      SELECT b.id as booking_id, b.start_time, b.end_time, b.is_paid, b.cost,
             ps.id as parking_spot_id, ps.spot_number, ps.is_occupied,
             v.id as vehicle_id, v.license_plate, v.brand, v.model,
             o.id as owner_id, o.full_name, o.phone_number
      FROM bookings b
      JOIN parking_spots ps ON b.parking_spot_id = ps.id
      JOIN vehicles v ON b.vehicle_id = v.id
      JOIN owners o ON v.owner_id = o.id
      WHERE UPPER(v.license_plate) LIKE UPPER(?)
      ORDER BY o.full_name
      """;

    return jdbcTemplate.query(sql, (rs, rowNum) -> mapBookings(rs), "%" + licensePlate + "%");
  }

  @Override
  public List<Booking> findByOwnerFullName(String fullName){
    String sql = """
      SELECT b.id as booking_id, b.start_time, b.end_time, b.is_paid, b.cost,
             ps.id as parking_spot_id, ps.spot_number, ps.is_occupied,
             v.id as vehicle_id, v.license_plate, v.brand, v.model,
             o.id as owner_id, o.full_name, o.phone_number
      FROM bookings b
      JOIN parking_spots ps ON b.parking_spot_id = ps.id
      JOIN vehicles v ON b.vehicle_id = v.id
      JOIN owners o ON v.owner_id = o.id
      WHERE LOWER(o.full_name) LIKE LOWER(?)
      ORDER BY o.full_name
      """;

    return jdbcTemplate.query(sql, (rs, rowNum) -> mapBookings(rs), "%" + fullName + "%");
  }

  @Override
  public List<Booking> findByVehicleLicensePlateAndOwnerFullName(String licensePlate, String fullName){
    String sql = """
      SELECT b.id as booking_id, b.start_time, b.end_time, b.is_paid, b.cost, ps.id as parking_spot_id, ps.spot_number, ps.is_occupied, v.id as vehicle_id, v.license_plate, v.brand, v.model, o.id as owner_id, o.full_name, o.phone_number
      FROM bookings b
      JOIN parking_spots ps ON b.parking_spot_id = ps.id
      JOIN vehicles v ON b.vehicle.id = v.id
      JOIN owners o ON v.owner_id = o.id
      WHERE UPPER(v.license_plate) LIKE UPPER(?) AND LOWER(o.full_name) LIKE LOWER(?)
      ORDER BY o.full_name
      """;
    
    return jdbcTemplate.query(sql,(rs, rowNum) -> mapBookings(rs));
  }

  @Override
  public List<Booking> findByEndTime(Instant endTime){
    String sql = """
      SELECT b.id as booking_id, ps.id as parking_spot_id
      FROM bookings b
      JOIN parking_spots ps ON b.parking_spot_id = ps.id
      WHERE b.end_time < ?
      """;

    return jdbcTemplate.query(sql, (rs, rowNum) -> {
      ParkingSpot parkingSpot = new ParkingSpot();
      parkingSpot.setId(rs.getObject("parking_spot_id", UUID.class));

      Booking booking = new Booking();
      booking.setId(rs.getObject("booking_id", UUID.class));
      booking.setParkingSpot(parkingSpot);

      return booking;
    }, Timestamp.from(endTime));
  }

  @Override
  public Booking create(Booking booking){
    if (booking.getId() == null) {
      booking.setId(UUID.randomUUID());
    }

    String sql = """
      INSERT INTO bookings (id, start_time, end_time, is_paid, cost, vehicle_id, parking_spot_id)
      VALUES (?, ?, ?, ?, ?, ?, ?)
      """;
    jdbcTemplate.update(sql, booking.getId(), Timestamp.from(booking.getStartTime()),
      Timestamp.from(booking.getEndTime()), booking.isPaid(), booking.getCost(),booking.getVehicle().getId(),
      booking.getParkingSpot().getId());

    return booking;
  }

  @Override
  public Booking update(Booking booking){
    String sql = """
      UPDATE bookings
      SET start_time = ?, end_time = ?, is_paid = ?, cost = ?, vehicle_id = ?, parking_spot_id = ?
      WHERE id = ?
      """;
    jdbcTemplate.update(sql,Timestamp.from(booking.getStartTime()), Timestamp.from(booking.getEndTime()),
      booking.isPaid(), booking.getCost(), booking.getVehicle().getId(),
      booking.getParkingSpot().getId(), booking.getId());

    return booking;
  }

  @Override
  public boolean deleteById(UUID id){
    String sql = "DELETE FROM bookings WHERE id = ?";
    int affectedRowsCount = jdbcTemplate.update(sql, id);

    return affectedRowsCount > 0;
  }

  @Override
  public boolean deleteAllByIds(List<UUID> idsList){
    if (idsList == null || idsList.isEmpty()) {
      return false;
    }

    String ids = String.join(",", Collections.nCopies(idsList.size(), "?"));
    String sql = String.format("DELETE FROM bookings WHERE id IN (%s)", ids);
    int affectedRowsCount = jdbcTemplate.update(sql, idsList.toArray());

    return affectedRowsCount > 0;
  }

  @Override
  public boolean existsById(UUID id){
    String sql = "SELECT COUNT(*) FROM bookings WHERE id = ?";
    Integer count = jdbcTemplate.queryForObject(sql, Integer.class, id);

    return count != null && count > 0;
  }

  @Override
  public boolean existsByParkingSpotIdOrVehicleId(UUID parkingSpotId, UUID vehicleId){
    String sql = "SELECT COUNT(*) FROM bookings WHERE parking_spot_id = ? OR vehicle_id = ?";
    Integer count = jdbcTemplate.queryForObject(sql, Integer.class, parkingSpotId, vehicleId);

    return count != null && count > 0;
  }
}
