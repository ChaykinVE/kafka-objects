package common.simple;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class CallbackContext {
    private ResponseHolder responseHolder;
    private CallbackArgument customCallbackArg;
}
