package kafka_test

import scala.collection.immutable.Seq
import scala.util.Random

object CarTrafficDummyData {

  private val carIds = Seq(1, 2)
  private val cities = Seq("Wroclaw", "Cracow")
  private val streets = Seq("Sezamowa", "Tunelowa")

  case class CarId(value: Int)

  case class CarSpeed(value: Int)
  case class CarEngine(rpm: Int, fuelLevel: Double)
  case class CarLocation(locationId: LocationId)

  case class DriverNotification(msg: String)

  case class LocationId(city: String, street: String)
  case class LocationData(speedLimit: Int, trafficVolume: TrafficVolume.Value, gasStationNearby: Boolean)

  object TrafficVolume extends Enumeration {
    val Low, Medium, High = Value
  }

  def carSpeed: Seq[(CarId, CarSpeed)] =
    for {
      carId <- carIds
      speed = (Random
        .nextInt(5) + 5) * 10
    } yield CarId(carId) -> CarSpeed(speed)

}
