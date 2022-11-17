# Pixie

<details><summary>Available modules</summary>
<p>

```
math
files
sockets
```
</p>
</details>

## Pixie as jar

Open console and write ``java -jar pixie.jar <path_to_your_pixie>`` in pixie.jar directory

## Pixie as lib

In your main function just write:
``
Pixie.execute(<path_to_your_pixie_file>)
``
or
``
Pixie.executeResource(<path_to_your_pixie_file_inside_resources>, CurrentClass.class)
``

### Syntex

<details><summary>Classes</summary>
<p>

  You can define class by ``class``
  
  Class example:
  ```py
  class Test {
      import math

      field f_str = ''
      field f_val = 0

      function @init(f_str, f_val){
      }

      function @add(other) {
          return f_str + other
      }

      function @inv() {
          return !f_val
      }

      function test(string, test) {
          return string + test + dsin(f_val)
      }
  }
  ```
  
  Classes uses ther own imports, you can see that in the example

  For use class you need print their contructor ``init Test('Some text', 100)``

  You also can define not classed instance by ``init(<var_name>: <var_value>)``
</p>
</details>

<details><summary>Variables</summary>
<p>

  You can define variable by ``var``

  Example: ``var <variable_name> = <value>``

  For string values you need write it inside apostrophes

  Example: ``'Hello world!'``
</p>
</details>

<details><summary>Import</summary>
<p>

  You can import new module by ``import <some_module>`` or import modules by ``import <some_module>, <more_modules>``
</p>
</details>


<details><summary>Function</summary>
<p>

  You can define your function with ``def``

  ```py
  def test_funct(arg0, arg1) {
    print(arg0 + arg1)
  }
  ```
</p>
</details>

<details><summary>Math</summary>
<p>

  Use ``import math`` for more math functions

  ``4 + 5 * 2`` will return ``18``

  ``5 * 2 + 4`` will return ``14``

  Same with ``/``
  </p>
  </details>

  <details><summary>For</summary>
  <p>

  For:
  ```py
  for (0, i, <, 4, 1) {
    print(i)
  }
  ```

  Foreach:
  ```py
  var inst = init(x: 1, y: 4)
  for (inst, value, key) {
    print(key + ': ' + value)
  }
  ```

  While
  ```py
  for (<bool>) {
    #if return value is false for will breaked
    return <bool>
  }
  ```
</p>
</details>

### Modules
You free to write your own Pixie module and use them inside your Pixie code
<details><summary>Here you can see little example module</summary>
<p>

```java
public class Run {
     public static void main(String[] args) {
          Pixie.addModule("example", new ExampleModule());
     }

     public static class ExampleModule extends PixieModule {
          public ExampleModule() {
               variables = Map.ofEntries(
                       Map.entry("example_variable", new NumValue(5))
               );
               functions = Map.ofEntries(
                       Map.entry("example_function",
                               function(
                                       (LineParser self) -> {
                                            System.out.println("Real example");
                                            return new NullValue();
                                       }
                               )
                       ),
                       Map.entry("example_function_one",
                               function(
                                       (LineParser self) -> {
                                            try {
                                                 String inside = one(self, "example_function_one");
                                                 System.out.println("Real example: " + parseText(self, inside));
                                            } catch (SyntaxException e) {
                                                 new SyntaxException(e.getMessage()).printStackTrace();
                                            }
                                            return new NullValue();
                                       }
                               )
                       ),
                       Map.entry("example_function_base",
                               function(
                                       (LineParser self) -> {
                                            try {
                                                 String[] inside = base(self, "example_function_base");
                                                 System.out.println("Real example: " + parseText(self, inside[0]) + " : " + parseText(self, inside[1]));
                                            } catch (SyntaxException e) {
                                                 new SyntaxException(e.getMessage()).printStackTrace();
                                            }
                                            return new NullValue();
                                       }
                               )
                       )
               );
          }
     }
}
```
</p>
</details>
