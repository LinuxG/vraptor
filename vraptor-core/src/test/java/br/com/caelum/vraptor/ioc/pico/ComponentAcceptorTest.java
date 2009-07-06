package br.com.caelum.vraptor.ioc.pico;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.ComponentRegistry;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.ioc.Component;

public class ComponentAcceptorTest {

    private ComponentAcceptor acceptor;
    private Mockery mockery;
    private ComponentRegistry registry;

    @Before
    public void setup() {
        mockery = new Mockery();
        registry = mockery.mock(ComponentRegistry.class);
        this.acceptor = new ComponentAcceptor(registry);
    }

    @Test
    public void shouldAcceptComponentsAnnotatedWithComponentAnnotation() {
        mockery.checking(new Expectations() {
            {
                one(registry).register(ComponentAnnotated.class, ComponentAnnotated.class);
            }
        });
        acceptor.analyze(ComponentAnnotated.class);
        mockery.assertIsSatisfied();
    }

    @Test
    public void shouldNotAcceptComponentsAnnotatedWithResourceAnnotation() {
        mockery.checking(new Expectations());
        acceptor.analyze(ResourceAnnotated.class);
        mockery.assertIsSatisfied();
    }

    @Test
    public void ignoresNonAnnotatedComponents() {
        acceptor.analyze(ComponentNotAnnotated.class);
        mockery.assertIsSatisfied();
    }
    
    @Test
    public void shouldRegisterComponentUsingDefinedInterface() {
        mockery.checking(new Expectations(){{
            one(registry).register(Runnable.class, RunnableComponent.class);
            one(registry).register(RunnableComponent.class, RunnableComponent.class);
        }});
        acceptor.analyze(RunnableComponent.class);
        mockery.assertIsSatisfied();
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void shouldNotRegisterComponentUsingAnUnimplementedInterface() {
        acceptor.analyze(OtherRunnableComponent.class);
    }

    class ComponentNotAnnotated {
    }

    @Resource
    class ResourceAnnotated {
    }

    @Component
    class ComponentAnnotated {
    }

    @Component(Runnable.class)
    class RunnableComponent implements Runnable {
		public void run() {	}
    }

    @Component(Comparable.class)
    class OtherRunnableComponent implements Runnable {
		public void run() {	}
    }
}
