package kafka_test

import io.circe.generic.auto._
import org.apache.kafka.streams.kstream.GlobalKTable
import org.apache.kafka.streams.scala.ImplicitConversions._
import org.apache.kafka.streams.scala.StreamsBuilder
import org.apache.kafka.streams.scala.kstream.{KStream, KTable}

class KafkaBasics {
  object Topics {
    final val OrdersByUserTopic = "orders-by-user"
    final val DiscountProfilesByUserTopic = "discount-profiles-by-user"
    final val DiscountsTopic = "discounts"
    final val OrdersTopic = "orders"
    final val PaymentsTopic = "payments"
    final val PaidOrdersTopic = "paid-orders"

    import Domain._
    import com.yon.kafka.Serialization._

    val builder = new StreamsBuilder()
    val userOrdersStream: KStream[UserId, Order] = builder.stream[UserId, Order](OrdersByUserTopic)

    // KTable - is distributed among the nodes
    val userProfilesTable: KTable[UserId, Profile] = builder.table[UserId, Profile](DiscountProfilesByUserTopic)

    // GlobalKTable - is copied on each node, so use only a few values, used for performance improvement - joining with GlobalKTable is faster - no shuffling
    val discountProfilesGTable: GlobalKTable[UserId, Discount] = builder.globalTable[UserId, Discount](DiscountsTopic)

    // KStream transformation
    val expensiveOrders: KStream[UserId, Order] = userOrdersStream.filter { (userId, order) => order.amount > 1000 }

    val listsOfProducts: KStream[UserId, List[Product]] = userOrdersStream.mapValues(_.products)
    val streamOfProducts: KStream[UserId, Product] = userOrdersStream.flatMapValues(_.products)

    // Join with the profiles table, both are key'd by UserId
//    val ordersWithUserProfiles: KStream[UserId, Order] = userOrdersStream.join(userProfilesTable)((order, profile) => {
//      (order, profile)
//    })

    builder.build()
  }

  object Domain {
    type UserId = String
    type Profile = String
    type Product = String
    type OrderId = String

    case class Order(orderId: OrderId, user: UserId, products: List[Product], amount: Double)
    case class Discount(profile: Profile, amount: Double) // in percentage points
    case class Payment(orderId: OrderId, status: String)
  }

}
