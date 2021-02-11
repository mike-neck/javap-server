javap-server
===

This service returns a javap result from a user java code.

How to run
---

Run Spring Boot Application.

```shell
./gradlew bootRun
```

Send your java code

```shell
cat <<EOF | gzip | base64 | tr '/' '_' | tr '+' '-' | awk '{print "http://localhost:8080/javap/"$1}' | xargs curl
public class Task {
  final int value;
  final int priority;
  public Task(int value, int priority) {
    this.value = value;
    this.priority = priority;
  }
  public int getValue() {
    return value;
  }
  public int getPriority() {
    return priority;
  }
  public int orderFactor() {
    return value * priority;
  }
}
EOF
```

The result will be as follows.

```json
{
  "result": "success",
  "contents": "Classfile /var/folders/83/bjplxlys1t57fz5rsn48k55r0000gn/T/Task3620568588988230576.class\n  Last modified 2021/02/11; size 450 bytes\n  MD5 checksum 376bd2be435f529f1bf633d6df8172f4\n  Compiled from \"Task.java\"\npublic class Task\n  minor version: 0\n  major version: 55\n  flags: (0x0021) ACC_PUBLIC, ACC_SUPER\n  this_class: #4                          // Task\n  super_class: #5                         // java/lang/Object\n  interfaces: 0, fields: 2, methods: 4, attributes: 1\nConstant pool:\n   #1 = Methodref          #5.#19         // java/lang/Object.\"<init>\":()V\n   #2 = Fieldref           #4.#20         // Task.value:I\n   #3 = Fieldref           #4.#21         // Task.priority:I\n   #4 = Class              #22            // Task\n   #5 = Class              #23            // java/lang/Object\n   #6 = Utf8               value\n   #7 = Utf8               I\n   #8 = Utf8               priority\n   #9 = Utf8               <init>\n  #10 = Utf8               (II)V\n  #11 = Utf8               Code\n  #12 = Utf8               LineNumberTable\n  #13 = Utf8               getValue\n  #14 = Utf8               ()I\n  #15 = Utf8               getPriority\n  #16 = Utf8               orderFactor\n  #17 = Utf8               SourceFile\n  #18 = Utf8               Task.java\n  #19 = NameAndType        #9:#24         // \"<init>\":()V\n  #20 = NameAndType        #6:#7          // value:I\n  #21 = NameAndType        #8:#7          // priority:I\n  #22 = Utf8               Task\n  #23 = Utf8               java/lang/Object\n  #24 = Utf8               ()V\n{\n  final int value;\n    descriptor: I\n    flags: (0x0010) ACC_FINAL\n\n  final int priority;\n    descriptor: I\n    flags: (0x0010) ACC_FINAL\n\n  public Task(int, int);\n    descriptor: (II)V\n    flags: (0x0001) ACC_PUBLIC\n    Code:\n      stack=2, locals=3, args_size=3\n         0: aload_0\n         1: invokespecial #1                  // Method java/lang/Object.\"<init>\":()V\n         4: aload_0\n         5: iload_1\n         6: putfield      #2                  // Field value:I\n         9: aload_0\n        10: iload_2\n        11: putfield      #3                  // Field priority:I\n        14: return\n      LineNumberTable:\n        line 4: 0\n        line 5: 4\n        line 6: 9\n        line 7: 14\n\n  public int getValue();\n    descriptor: ()I\n    flags: (0x0001) ACC_PUBLIC\n    Code:\n      stack=1, locals=1, args_size=1\n         0: aload_0\n         1: getfield      #2                  // Field value:I\n         4: ireturn\n      LineNumberTable:\n        line 9: 0\n\n  public int getPriority();\n    descriptor: ()I\n    flags: (0x0001) ACC_PUBLIC\n    Code:\n      stack=1, locals=1, args_size=1\n         0: aload_0\n         1: getfield      #3                  // Field priority:I\n         4: ireturn\n      LineNumberTable:\n        line 12: 0\n\n  public int orderFactor();\n    descriptor: ()I\n    flags: (0x0001) ACC_PUBLIC\n    Code:\n      stack=2, locals=1, args_size=1\n         0: aload_0\n         1: getfield      #2                  // Field value:I\n         4: aload_0\n         5: getfield      #3                  // Field priority:I\n         8: imul\n         9: ireturn\n      LineNumberTable:\n        line 15: 0\n}\nSourceFile: \"Task.java\"\n"
}
```

If you have `jq` command(or `gojq`), extracting `.contents` with `-r` option may generates your familiar javap outputs.   

```text
Classfile /var/folders/83/bjplxlys1t57fz5rsn48k55r0000gn/T/Task3158875499982333987.class
  Last modified 2021/02/11; size 450 bytes
  MD5 checksum 376bd2be435f529f1bf633d6df8172f4
  Compiled from "Task.java"
public class Task
  minor version: 0
  major version: 55
  flags: (0x0021) ACC_PUBLIC, ACC_SUPER
  this_class: #4                          // Task
  super_class: #5                         // java/lang/Object
  interfaces: 0, fields: 2, methods: 4, attributes: 1
Constant pool:
   #1 = Methodref          #5.#19         // java/lang/Object."<init>":()V
   #2 = Fieldref           #4.#20         // Task.value:I
   #3 = Fieldref           #4.#21         // Task.priority:I
   #4 = Class              #22            // Task
   #5 = Class              #23            // java/lang/Object
   #6 = Utf8               value
   #7 = Utf8               I
   #8 = Utf8               priority
   #9 = Utf8               <init>
  #10 = Utf8               (II)V
  #11 = Utf8               Code

... outputs omitted
```
