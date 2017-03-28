# AndroidKVO
this library is for helping android developer to implement something like iOS key value observer pattern (KVO)

# What is KVO?

If you came a cross KVO in iOS that feature will certainly caught your eyes, why?
well imagine that you have an object in your memory and you want to know if any change happened to that object during the life cycle of your application, well KVO give you that ability but unfortunately android doesn't support that feature

# Features supported by Lib

- you can listen to any change happen to a certain field inside a certain object and whenever that reference change you will 
get notified.
- you can listen to any change happen to a certain field accross all of your models for example
imagine that you got an object Person will field called salary and another object Payroll with field called balance but both 
of the fields are the same
well you can give the same id to both fields and whenever a change happen to any of them in any object (even if it's 
a new object and you didn't assign a listener to it) you will get notified
- no need to worry about memory leak since Lib use only `WeakReference` 
- if your listener is `Activity` or `Fragment` before invoke callback the lib will check `isFinishing()` to make sure that they are able to receive callbacks

#### to simplify the previous the lib give you the ability to be notified on object level or to be notified on application level

# How To Use

use jCenter() to download it.   
```
compile 'com.ark.android:AndroidKVO:1.0.0'
annotationProcessor 'com.ark.android:AndroidKVO-Compiler:1.0.0'
```

- first let us consider you have a model called `Person` with variables such as `name` , `age` and salary
- another object called `Payroll` with only one variable called `balance`
- and 2 activities one called HomeActivity and the other is PayrollDetails where you can change the person variables and payroll variable
- let us imagine that we need to observe any change happen to name,age fields in a particular object (reference) and any change happen to salary in any object new or exist

### Implementation

- First inside `Person` class lets annotate it with `@AndroidKVO`
- Annotate name and age with `@KVOField` 
- Annotate `salary` with `@KVOField(id = "salary")` where ID is any unique string of your choosing
- Do the same in Payroll object and annotate balance with same id you gave to salary in our case `@KVOField(id = "salary")`
- Your object will look something like that

```
@AndroidKVO
public class Person {
    @KVOField(id = "name")
    public String name;
    @KVOField
    public int age;
    @KVOField(id = "salary")
    public int salary;
}
```
- Build your project 
- Now you can use two new objects called (yourObject-KVO) so in our case PersonKVO and PayrollKVO
- Inside your HomeActivity implement `KVOListener` and override `onValueChange(Object affectedObject, Object changedValue, String fieldNameOrId)`
- Now init the KVO objects and assign HomeActivity as listener if you need to get notified if any change happen to anyField with KVOField annotation
```
PersonKVO person = new PersonKVO();
person.setListener(KVOListener);
```
#### OR

if you need to get notified only when a change happen to a certain field

```
person.setListener(KVOListener, PersonKVO.FieldName.name);
```

- Now pass this object to PayrollDetails activity through intent and change any field by calling the setter the HomeActivity will get notified with the change
- If however we call the following line inside HomeActivity
```
person.setListenerForId(KVOListener , "salary");
```
- If we declared a new object (PersonKVO or PayrollKVO) in any other class and we called setSalary or setBalance the HomeActivity will still get notified

# Methods provided by lib

- `FieldName` enum contain all fields name in your model
- `setListener()` make class observe for any change happen to any field with `@KVOField` annotation and if that field contain ID the lib will add a that listener as an Application level observer (if any change happen to that field in any object new or old the listener will be notified)
- `setListener(KVOListener listener , FieldName property)` make class observe only for that field in that particular object (object level)
- `setListenerForId(KVOListener listener, String id)` set the class as an application level observer for that id
- `removeListener()` remove that observer from list

###License

    The MIT License (MIT)

    Copyright (c) 2017 Ahmed basyouni

    Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

    The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

