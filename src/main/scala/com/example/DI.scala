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
  class Authentication(storage: persister.Storage) {
    def login = storage.read
  }

  trait AuthenticationModule extends PluginModule {
    override def init = {
      super.init
      println("AuthenticationModule")
    }

    lazy val authentication = new Authentication(storage)
    def storage: persister.Storage
  }
}

package persister {
  class Database() extends persister.Storage {
    def read = "Read from database"
  }

  trait DatabaseModule extends persister.StorageModule {
    override def init = {
      super.init
      println("DatabaseModule")
    }

    lazy val storage = new Database
  }
}

package persister {
  class MemStorage() extends persister.Storage {
    def read = "Read from memory"
  }

  trait MemStorageModule extends persister.StorageModule {
    override def init = {
      super.init
      println("MemStorageModule")
    }

    lazy val storage = new MemStorage
  }
}

object DI extends App {
  val databaseModules = new authentication.AuthenticationModule
    with persister.DatabaseModule

  val memoryModules = new authentication.AuthenticationModule
    with persister.MemStorageModule

  memoryModules.init
  databaseModules.init

  println(memoryModules.authentication.login)
  println(databaseModules.authentication.login)
}
