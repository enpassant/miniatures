package com.example

trait PluginModule {
  //def init
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
    lazy val authentication = new Authentication(storage)
    def storage: persister.Storage
  }
}

package persister {
  trait DatabaseModule extends persister.StorageModule {
    class Database() extends persister.Storage {
      def read = "Read from database"
    }

    lazy val storage = new Database
  }
}

package persister {
  trait MemStorageModule extends persister.StorageModule {
    class MemStorage() extends persister.Storage {
      def read = "Read from memory"
    }

    lazy val storage = new MemStorage
  }
}

object DI extends App {
  val databaseModules = new authentication.AuthenticationModule
    with persister.DatabaseModule

  val memoryModules = new authentication.AuthenticationModule
    with persister.MemStorageModule

  println(memoryModules.authentication.login)
  println(databaseModules.authentication.login)
}
