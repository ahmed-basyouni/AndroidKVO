# AndroidKVO
this library is for helping android developer to implement something like iOS key value observer pattern (KVO)

# What is KVO?

If you came a cross KVO in iOS that feature will certainly caught your eyes, why?
well imagine that you have an object in your memory and you want to know if any change happened to that object during the life cycle of your application, well KVO give you that ability but unfortunately android doesn't support that feature

# Features supported by Lib

- you can listen to any change happen to a certain field inside a certain object and whenever that reference change you will 
get notified.
- you can listen to any change happen to a certain field accross all of your models for example
imagin that you got an object Person will field called salary and another object Payroll with field called balance but both 
of the fields are the same
well you can give the same id to both fields and whenever a change happen to any of them in any object (even if it's 
a new object and you didn't assign a listener to it) you will get notified
- no need to worry about memory leak since Lib use only `WeakReference` 
- if your listener is `Activity` or `Fragment` before invoke callback the lib will check `isFinishing()` to make sure that they are able to recieve callbacks

# How To Use


