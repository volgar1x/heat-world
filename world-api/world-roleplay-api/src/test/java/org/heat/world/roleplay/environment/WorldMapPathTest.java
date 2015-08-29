package org.heat.world.roleplay.environment;

import org.junit.Before;
import org.junit.Test;

import static com.ankamagames.dofus.network.enums.DirectionsEnum.DIRECTION_NORTH_EAST;
import static com.ankamagames.dofus.network.enums.DirectionsEnum.DIRECTION_SOUTH_EAST;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.heat.shared.tests.CollectionMatchers.hasSize;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class WorldMapPathTest {
    private WorldPosition position;

    @Before
    public void setUp() throws Exception {
        position = mock(WorldPosition.class);
        when(position.isResolved()).thenReturn(true);
    }

    @Test
    public void testParseNode() throws Exception {
        // given
        short key = 29027;

        // when
        WorldMapPath.Node node = WorldMapPath.Node.parse(key);

        // then
        assertThat(node.getPoint().cellId, equalTo((short) 355));
        assertThat(node.getDir(), equalTo(DIRECTION_NORTH_EAST));
    }

    @Test
    public void testNodeToShort() throws Exception {
        // given
        WorldMapPath.Node node = new WorldMapPath.Node(WorldMapPoint.get(355), DIRECTION_NORTH_EAST);

        // when
        short key = node.export();

        // then
        assertThat(key, equalTo(key));
    }

    @Test
    public void testParse() throws Exception {
        // given
        short[] keys = new short[]{ 29027, 24837, 24809 };

        // when
        WorldMapPath path = WorldMapPath.parse(position, keys);

        // then
        assertThat(path.getNodes(), hasSize(9));
        assertThat(path.origin().getPoint().cellId, equalTo((short) 355));
        assertThat(path.target().getPoint().cellId, equalTo((short) 233));

        /////////////////////////////////////////////////////////////

        // given
        short[] keysB = new short[]{
                new WorldMapPath.Node(WorldMapPoint.of(0).get(), DIRECTION_SOUTH_EAST).export(),
                new WorldMapPath.Node(WorldMapPoint.of(391).get(), DIRECTION_SOUTH_EAST).export(),
        };

        // when
        WorldMapPath pathB = WorldMapPath.parse(position, keysB);

        // then
        assertThat(pathB.getNodes(), hasSize(28));
        assertThat(pathB.origin().getPoint().cellId, equalTo((short) 0));
        assertThat(pathB.target().getPoint().cellId, equalTo((short) 391));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testParseFailure() throws Exception {
        WorldMapPath.parse(position, new short[1]);
    }

    @Test
    public void shouldContainASpecificPoint() throws Exception {
        WorldPosition pos = mock(WorldPosition.class);
        when(pos.isResolved()).thenReturn(true);

        short[] points = new short[]{24879, 28863, 24727, 24699};
        WorldMapPoint point = WorldMapPoint.get(178);
        WorldMapPath path = WorldMapPath.parse(pos, points);

        assertTrue("A path should contain a specific point", path.contains(point));
    }
}