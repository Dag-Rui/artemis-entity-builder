package no.daffern.artemis.dummy;

import com.artemis.Component;

public class TestComponent extends Component {

  public int test;
  public int test1;
  public int test2;
  public int test3;
  public int test4;

  public String something;

  public void set(int test, int test3, String something){
    this.test = test;
    this.test3 = test3;
    this.something = something;
  }

  public void set(int test, String something){
    this.test = test;
    this.something = something;
  }
}
