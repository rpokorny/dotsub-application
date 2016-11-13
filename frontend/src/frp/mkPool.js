import * as Kefir from 'kefir';

/**
 * This function creates pluggable Kefir streams - streams that
 * can have new values explicitly added to them.  The returned object
 * has three properties:
 * sendAction: the writing end.  Call this function to add something to the stream
 * stream: the reading end.  Values passed to sendAction come out here
 * pool: The underlying Kefir pool object, which can be used to pass in entire
 * other streams
 */
export default function() {
    const pool = Kefir.pool(),
        stream = pool.map(x => x);

    return {
        pool: pool,
        stream: stream,
        sendAction: function(action) {
            pool.plug(Kefir.constant(action));
        }
    };
}
