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

## Pixie run launcher

Open `run.bat` or write in console `java -jar pixie.jar`

### Pixie in console

Open console and write ``java -jar pixie.jar <path_to_your_pixie>`` in pixie.jar directory

### Pixie as lib

In your main function just write:
``
Pixie.execute(<path_to_your_pixie_file>)
``
or
``
Pixie.executeResource(<path_to_your_pixie_file_inside_resources>, CurrentClass.class)
``

### Syntex
<details><summary>Example code</summary>
<p>

```py
def f(b)
{
    if (b){
        print("Yes");
    };
    else if (!b) print("No");
    else print("Cannot");
};

var b = true;
f(b);
b = !b;
f(b);

var inst = new instance(x: 4, y: "Lol in \"string\"");
print(inst.x);
print(inst.y);

class Test
{
    static function print(s0, s1)
    {
        print(s0 + s1);
    };

    static field y = "Lol";
    field s0 = "2";
    field s1 = "2";

    Test(s)
    {
        s0 = s;
    };

    Test(s0, s1)
    {
        this.s0 = s0;
        this.s1 = s1;
    };

    function print()
    {
        print(s0 + this.s1);
    };

    function print(s)
    {
        print(s);
    };
};

print(Test.y);
Test.print("4", "2");

var t = new Test("3");
t.print();

t = new Test("3", "4");
t.print();
t.print("5");

var y = ["a", "b", "c"];

for (i : y)
{
    print(i);
};

y += "d";

for (i : y)
    print(i);

for (i; var i = 0; i < 4; i++)
{
    print(i);
};

for (j; var j = 5; j >= 0; j--) print(j);
```
</p>
</details>

### Modules
You free to write your own Pixie module and use them inside your Pixie code
<details><summary>Example module</summary>
<p>

```java
public class Run {
     public static void main(String[] args) {
          Pixie.addModule("example", new ExampleModule());
     }

     public static class ExampleModule extends PixieModule {
          public ExampleModule() {
               variables = LineParser.ofEntries(
                       Map.entry("example_variable", new NumValue(5))
               );
               functions = LineParser.ofEntries(
                       function("example_function", 0,
                               (LineParser self) -> {
                                    System.out.println("Real example");
                                    return new NullValue();
                               }
                       ),
                       function("example_function_one", 1,
                               (LineParser self) -> {
                                    try {
                                         String[] inside = get(line, words);
                                         System.out.println("Real example: " + parseText(self, inside[0]));
                                    } catch (SyntaxException e) {
                                         new SyntaxException(e.getMessage()).printStackTrace();
                                    }
                                    return new NullValue();
                               }
                       ),
                       function("example_function_two", 2,
                               (String line, String[] words, LineParser p) -> {
                                    try {
                                         String[] inside = get(line, words);
                                         System.out.println("Real example: " + parseText(self, inside[0]) + " : " + parseText(self, inside[1]));
                                    } catch (SyntaxException e) {
                                         new SyntaxException(e.getMessage()).printStackTrace();
                                    }
                                    return new NullValue();
                               }
                       )
               );
          }
     }
}
```
</p>
</details>
