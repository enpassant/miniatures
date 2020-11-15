package immutable
import scala.concurrent.Future
import scala.concurrent.ExecutionContext

object AccountMutable extends App {
  class Account(var balance: Double) {
    def deposit(amount: Double): Unit = {
      balance += amount
    }

    def withdraw(amount: Double): Boolean = {
      if (balance >= amount) {
        balance -= amount
        true
      } else {
        false
      }
    }

    override def toString() = {
      s"Account($balance)"
    }
  }

  def sendMoney(source: Account, target: Account, amount: Double): Boolean = {
    source.synchronized {
      Thread.sleep(1)
      val success = source.withdraw(amount)
      if (success) {
        target.synchronized {
          Thread.sleep(1)
          target.deposit(amount)
        }
      }
      success
    }
  }

  def sendMoneyRefactored(source: Account, target: Account, amount: Double): Boolean = {
    val newTarget = target.deposit(amount)
    val success = source.withdraw(amount)
    success
  }

  def testSendMoney(sendMoney: (Account, Account, Double) => Boolean) = {
    val accountFred = new Account(100)
    val accountJoe = new Account(100)
    accountFred.deposit(10)
    accountFred.withdraw((180))
    println(s"accountFred: $accountFred, accountJoe: $accountJoe")
    sendMoney(accountFred, accountJoe, 70)
    println(s"accountFred: $accountFred, accountJoe: $accountJoe")
    sendMoney(accountFred, accountJoe, 70)
    println(s"accountFred: $accountFred, accountJoe: $accountJoe")
  }

  testSendMoney(sendMoney)
  testSendMoney(sendMoneyRefactored)

  implicit val ec = ExecutionContext.global
  def testSendMoneyPar() = {
    val accountFred = new Account(100)
    val accountJoe = new Account(100)
    println(s"accountFred: $accountFred, accountJoe: $accountJoe")
    val future1 = Future { sendMoney(accountFred, accountJoe, 70) }
    val future2 = Future { sendMoney(accountJoe, accountFred, 50) }
    val future = for {
      success <- future1
      _ <- future2
    } yield (success)
    future.foreach(
      success => println(s"success: $success, accountFred: $accountFred, accountJoe: $accountJoe")
    )
  }

  testSendMoneyPar()
  Thread.sleep(1000)
}
