package immutable
import scala.concurrent.ExecutionContext
import akka.compat.Future
import scala.concurrent.Future

object AccountImmutable extends App {
  case class Account(balance: Double) {
    def deposit(amount: Double): Account= {
      copy(balance = balance + amount)
    }

    def withdraw(amount: Double): Option[Account] = {
      if (balance >= amount) {
        Some(copy(balance = balance - amount))
      } else {
        None
      }
    }
  }

  def sendMoney(source: Account, target: Account, amount: Double) : (Account, Account) = {
    source.withdraw(amount).map(newSource =>
      (newSource, target.deposit(amount))
    ).getOrElse(source, target)
  }

  def sendMoneyRefactored(source: Account, target: Account, amount: Double) : (Account, Account) = {
    val newTarget = target.deposit(amount)
    source.withdraw(amount).map(newSource =>
      (newSource, newTarget)
    ).getOrElse(source, target)
  }

  def testSendMoney(sendMoney: (Account, Account, Double) => (Account, Account)) = {
    val accountFred = new Account(100)
    val accountJoe = new Account(100)
    val accountFred2 = accountFred.deposit(10)
    val accountFred3Opt = accountFred2.withdraw((180))
    val accountFred3 = accountFred3Opt.getOrElse(accountFred2)
    println(s"accountFred: $accountFred3, accountJoe: $accountJoe")
    val(accountFred4, accountJoe2) = sendMoney(accountFred3, accountJoe, 70)
    println(s"accountFred: $accountFred4, accountJoe: $accountJoe2")
    val(accountFred5, accountJoe3) = sendMoney(accountFred4, accountJoe2, 70)
    println(s"accountFred: $accountFred5, accountJoe: $accountJoe3")
  }

  testSendMoney(sendMoney)
  testSendMoney(sendMoneyRefactored)

  implicit val ec = ExecutionContext.global
  def testSendMoneyPar() = {
    val accountFred = new Account(100)
    val accountJoe = new Account(100)
    println(s"accountFred: $accountFred, accountJoe: $accountJoe")
    val future = for {
      (accountFred2, accountJoe2) <- Future { sendMoney(accountFred, accountJoe, 70) }
      (accountJoe3, accountFred3) <- Future { sendMoney(accountJoe2, accountFred2, 50) }
    } yield (accountFred3, accountJoe3)
    future.foreach {
      case (accountFred3, accountJoe3) =>
        println(s"accountFred3: $accountFred3, accountJoe3: $accountJoe3")
    }
  }

  testSendMoneyPar()
  Thread.sleep(1000)
}
