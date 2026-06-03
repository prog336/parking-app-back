package com.parkingapp.parkingappback.repositories.Impl;

import com.parkingapp.parkingappback.entities.ParkingSpot;
import com.parkingapp.parkingappback.repositories.ParkingSpotRepository;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@AllArgsConstructor
public class ParkingSpotRepositoryImpl implements ParkingSpotRepository {
  private final JdbcTemplate jdbcTemplate;

  private ParkingSpot mapParkingSpots(ResultSet rs) throws SQLException {
    ParkingSpot parkingSpot = new ParkingSpot();
    parkingSpot.setId(rs.getObject("id", UUID.class));
    parkingSpot.setSpotNumber(rs.getString("spot_number"));
    parkingSpot.setOccupied(rs.getBoolean("is_occupied"));

    return parkingSpot;
  }

  @Override
  public List<ParkingSpot> findAll(){
    String sql = "SELECT id, spot_number, is_occupied FROM parking_spots ORDER BY spot_number";

    return jdbcTemplate.query(sql, (rs, rowNum) -> mapParkingSpots(rs));
  }

  @Override
  public Optional<ParkingSpot> findById(UUID id){
    String sql = "SELECT id, spot_number, is_occupied FROM parking_spots WHERE id = ?";
    List<ParkingSpot> parkingSpots = jdbcTemplate.query(sql, (rs, rowNum) -> mapParkingSpots(rs), id);

    return parkingSpots.isEmpty() ? Optional.empty() : Optional.of(parkingSpots.getFirst());
  }

  @Override
  public ParkingSpot create(ParkingSpot parkingSpot){
    if (parkingSpot.getId() == null) {
      parkingSpot.setId(UUID.randomUUID());
    }

    String sql = "INSERT INTO parking_spots (id, spot_number, is_occupied) VALUES (?, ?, ?)";
    jdbcTemplate.update(sql, parkingSpot.getId(), parkingSpot.getSpotNumber(), parkingSpot.isOccupied());

    return parkingSpot;
  }

  @Override
  public ParkingSpot update(ParkingSpot parkingSpot){
    String sql = "UPDATE parking_spots SET spot_number = ?, is_occupied = ? WHERE id = ?";
    jdbcTemplate.update(sql, parkingSpot.getSpotNumber(), parkingSpot.isOccupied(), parkingSpot.getId());

    return parkingSpot;
  }

  @Override
  public boolean deleteById(UUID id){
    String sql = "DELETE FROM parking_spots WHERE id = ?";
    int affectedRowsCount = jdbcTemplate.update(sql, id);

    return affectedRowsCount > 0;
  }

  @Override
  public boolean existsById(UUID id){
    String sql = "SELECT COUNT(*) FROM parking_spots WHERE id = ?";
    Integer count = jdbcTemplate.queryForObject(sql, Integer.class, id);

    return count != null && count > 0;
  }

  @Override
  public boolean existsBySpotNumber(String spotNumber){
    String sql = "SELECT COUNT(*) FROM parking_spots WHERE spot_number = ?";
    Integer count = jdbcTemplate.queryForObject(sql, Integer.class, spotNumber);

    return count != null && count > 0;
  }

  @Override
  public boolean releaseAllByIds(List<UUID> idsList){
    if (idsList == null || idsList.isEmpty()) {
      return false;
    }

    String ids = String.join(",", Collections.nCopies(idsList.size(), "?"));
    String sql = String.format("UPDATE parking_spots SET is_occupied = false WHERE id IN (%s)", ids);
    int affectedRowsCount = jdbcTemplate.update(sql, idsList.toArray());

    return affectedRowsCount > 0;
  }
}
