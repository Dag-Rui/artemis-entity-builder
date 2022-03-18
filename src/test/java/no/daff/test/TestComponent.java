package no.daff.test;

import com.artemis.Component;

public class TestComponent extends Component {

  String s;
  SomeClass someClass;

  public void set(SomeClass someClass) {
    this.someClass = someClass;
  }

  public void set(SomeOtherClass someClass) {
  }

  public class SomeClass {
    int i = 0;

    public void someMethod() {

    }
  }
}
