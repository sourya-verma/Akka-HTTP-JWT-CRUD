package models

import java.sql.Date

case class Student(name : String, email : String, universityId: Int, id: Option[Int] = None)


case class UserCred(id:Int, firstName:String, lastName:String, password:String)

case class LoginDetail (id:Int, password:String)