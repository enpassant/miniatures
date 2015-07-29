package com.example

trait PluginModule {
  def init: Unit = {}
}

package persister {
  trait Storage {
    def read: String
  }

  trait StorageModule extends PluginModule {
    def storage: Storage
  }
}

package authentication {
  import persister._

  class Authentication(storage: persister.Storage) {
    def login = storage.read
  }

  trait AuthenticationModule extends PluginModule {
    override def init = {
      super.init
      println("AuthenticationModule")
    }

    lazy val authentication = new Authentication(storage)
    def storage: Storage
  }
}

package persister {
  class Database() extends Storage {
    def read = "Read from database"
  }

  trait DatabaseModule extends StorageModule {
    override def init = {
      super.init
      println("DatabaseModule")
    }

    lazy val storage = new Database
  }
}

package persister {
  class MemStorage() extends Storage {
    def read = "Read from memory"
  }

  trait MemStorageModule extends StorageModule {
    override def init = {
      super.init
      println("MemStorageModule")
    }

    lazy val storage = new MemStorage
  }
}

object DI extends App {
  import authentication._
  import persister._

  val databaseModules = new AuthenticationModule with DatabaseModule

  val memoryModules = new AuthenticationModule with MemStorageModule

  memoryModules.init
  databaseModules.init

  println(memoryModules.authentication.login)
  println(databaseModules.authentication.login)
}
