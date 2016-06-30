# JavaCommander

Java framework for annotating functions such that they can be called via interpreting string inputs. 

Example usages: command-line interface applications, chat applications, processing strings sent via sockets.

## Usage

Annotating functions of a class:

```java
public class SomeClass 
{
    @Command
    (
      names = { "printStringInArray", "psia"}, 
      description = "Print the string at the index of the string array.",
      options = { 
                  @Option
                  (
                    names = "-index", 
                    description = "The index of the string to print.",
                    hasDefaultValue = true,
                    defaultValue = "0"
                  ),
                  @Option
                  (
                    names = "-array",
                    description = "The string array.",
                    translator = StringArrayTranslator.class)
      }
    )
    public void printStringInArray(int index, String[] array)
    {
      System.out.println(array[index]);
    }
}
```

Fields in @Command's and @Option's may be completely empty, in which case default values are used.

Registering an instance of a class with a JavaCommander instance:

```java
JavaCommander jc = new JavaCommander();
SomeClass sc = new SomeClass();
jc.registerObject(sc);
```

Interpreting and executing a string input:

```java
String input = "printStringInArray -index 1 -array 'One,Two,Three'";
jc.execute(input);
```

Implementation of the custom StringArrayTranslator class, which translates a string to a string array:

```java
public class StringArrayTranslator implements OptionTranslator<String[]>
{
    @Override
    public String[] translateString(String s) throws JavaCommanderException
    {
        return s.split(",");
    }
}
```
