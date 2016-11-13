import * as Immutable from 'immutable';

export default Immutable.Record({
    title: '',
    description: '',
    filename: ''    //this is the filename as reported by the file input element.
                    //Due to the way file inputs work, this name isn't particularly
                    //accurate but it can be reset to empty in order to reset the file upload
});
