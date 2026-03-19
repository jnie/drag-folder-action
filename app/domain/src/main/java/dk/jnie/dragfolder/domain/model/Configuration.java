package dk.jnie.dragfolder.domain.model;

import dk.jnie.dragfolder.domain.util.ObjectStyle;
import org.immutables.value.Value;

@ObjectStyle
@Value.Immutable
public interface ConfigurationDef {
    String getMonitorFolder();
    String getOutputFolder();
    int getTimerSeconds();
    boolean getClearMonitorFolder();
}