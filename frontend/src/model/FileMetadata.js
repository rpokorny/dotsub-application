import * as Immutable from 'immutable';

export default Immutable.Record({
    id: null,
    title: '',
    description: '',
    mediaType: '',
    filename: '',
    href: '',
    creationDate: new Date(0)
});
