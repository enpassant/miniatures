package tight

import java.util.concurrent.ConcurrentHashMap

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

object Cooking extends App {
  class KVStore[K, V] {
    private val s = new ConcurrentHashMap[K, V]()

    def create(k: K, v: V): Future[Boolean] = Future.successful(s.putIfAbsent(k, v) == null)
    def read(k: K): Future[Option[V]] = Future.successful(Option(s.get(k)))
    def update(k: K, v: V): Future[Unit] = Future.successful(s.put(k, v))
    def delete(k: K): Future[Boolean] = Future.successful(s.remove(k) != null)
  }

  //

  type FoodName = String
  type Quantity = Int
  type FoodKV = KVStore[String, Int]
  type TestFoodKV = KVStore[String, Int]

  def addFood(n: FoodName, q: Quantity): Future[Unit] = for {
    current <- fc.read(n)
    updated = current.map(c => c + q).getOrElse(q)
    _ <- fc.update(n, updated)
  } yield ()

  def takeFood(n: FoodName, q: Quantity): Future[Quantity] = for {
    current <- fc.read(n)
    inStock = current.getOrElse(0)
    taken = Math.min(inStock, q)
    left = inStock - taken
    _ <- if (left > 0) fc.update(n, left) else fc.delete(n)
  } yield taken

  //

  def cookSauce(q: Quantity): Future[Quantity]=
    for {
      tomatoQ <- takeFood("tomato", q)
      vegQ <- takeFood("non-tomato veggies", q)
      _ <- takeFood("garlic", q*2)
      sauceQ = tomatoQ/2 + vegQ*3/4
      _ <- addFood("sauce", sauceQ)
    } yield sauceQ

  def cookPasta(q: Quantity): Future[Quantity]=
    for {
      pastaQ <- takeFood("pasta", q)
      _ <- takeFood("salt", 10)
      _ <- addFood("cooked pasta", pastaQ)
    } yield pastaQ

  //

  val test = true
  val fc = if (test) new TestFoodKV else new FoodKV

  //

  val shopping = for {
    _ <- addFood("tomato", 10)
    _ <- addFood("non-tomato veggies", 15)
    _ <- addFood("garlic", 42)
    _ <- addFood("salt", 1)
    _ <- addFood("pasta", 5)
  } yield ()

  val cooking = for {
    _ <- shopping
    sq <- cookSauce(5)
    pq <- cookPasta(10)
  } yield s"Cooked $sq sauce and $pq pasta"

  val eating = Await.result(cooking, 1.minute)
  println(eating)
}


