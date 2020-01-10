package no.daffern.artemis.dummy;

import com.artemis.Component;

public class TestComponent2 extends Component {

  public int test;
  public int test1;
  public int test2;
  public int test3;
  public int test4;

  public String something;

  public void set(int test, int test3, String something, int test4){
    this.test = test;
    this.test3 = test3;
    this.something = something;
    this.test4 = test4;
  }
}
