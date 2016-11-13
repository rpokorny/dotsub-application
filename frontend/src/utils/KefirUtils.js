/**
 * @param stream a Kefir Stream or Property
 * @return a stream like the input stream but with any downstream
 * exceptions on values caught and turned into stream errors.
 * Exceptions caught when sending errors downstream are not resent
 * as errors (as that would likely cause an infinite loop), but are logged
 * to the console
 */
export function exceptionsToErrors(stream) {
    return stream.withHandler(function(emitter, evt) {
        try {
            emitter.emitEvent(evt);
        }
        catch (e) {
            console.error(e);

            if (evt.type === 'value') {
                emitter.error(e);
            }
        }
    });
}
