package org.tafel.squating.domain.value;

import java.util.List;

public record MailSnapshot(
    boolean hasMx,
    List<String> mxHosts,
    boolean hasSpfRecord,
    boolean hasDmarcRecord,
    boolean isMailableThreat
) {
    public static MailSnapshot safe() {
        return new MailSnapshot(false, List.of(), false, false, false);
    }
}
