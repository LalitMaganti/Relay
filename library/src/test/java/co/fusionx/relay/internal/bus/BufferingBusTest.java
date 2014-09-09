package co.fusionx.relay.internal.bus;

import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class BufferingBusTest {

    private FakeEventBus<Object> mInternalBus;

    private BufferingBus<Object> mBufferingBus;

    @Before
    public void setup() {
        mInternalBus = new FakeEventBus<>();
        mBufferingBus = new BufferingBus<>(mInternalBus);
    }

    @Test
    public void testBuffering() {
        final String event = "Test post";
        mBufferingBus.post(event);

        assertThat(mBufferingBus.getBuffer()).containsExactly(event);
    }

    @Test
    public void testForwarding() {
        final String event = "Test post";
        mBufferingBus.post(event);

        assertThat(mInternalBus.lastEvent()).isEqualTo(event);
    }
}