import * as Kefir from 'kefir';

import * as AsyncAction from './AsyncAction';

const fetchPool = Kefir.pool(),
    files = [{
        filename: 'file1.txt',
        title: 'file 1',
        mediaType: 'text/plain',
        id: '1234',
        description: 'the first file',
        creationDate: '1990-01-01T12:00:00Z',
        href: 'http://localhost/1'
    }, {
        filename: 'file2.txt',
        title: 'file 2',
        mediaType: 'text/csv',
        id: '1235',
        description: 'the second file',
        creationDate: '1990-01-01T04:00:00Z',
        href: 'http://localhost/2'
    }];

jest.mock('./Action', function() {
    return {
        SetFiles: val => ['SetFiles', val],
        AddFile: val => ['AddFile', val],
        ClearUpload: () => ['ClearUpload'],
        SetErrorMessage: val => ['SetErrorMessage', val]
    };
});

import * as FetchUtils from './utils/FetchUtils';

import Model from './model/Model';
import UploadModel from './model/UploadModel';

//only mock fetchStream not the rest of FetchUtils
FetchUtils.fetchStream = jest.fn(() => fetchPool);

const { fetchStream } = FetchUtils;

beforeEach(function() {
    fetchStream.mockClear();
});

describe('LoadFiles', function() {
    it('fetches from the backend URI with a content type of application/json', function(done) {
        const retval = AsyncAction.LoadFiles()();

        function handler() {
            expect(fetchStream.mock.calls[0][0]).toBe(process.env.REACT_APP_BACKEND_URI);
            expect(fetchStream.mock.calls[0][1]).toBeUndefined();
            expect(fetchStream.mock.calls[0][2]).toBeUndefined();
            expect(fetchStream.mock.calls[0][3]).toBe('application/json');

            retval.offValue(handler);
            done();
        }

        retval.onValue(handler);

        //trigger async response
        fetchPool.plug(Kefir.later(10, files));
    });

    it('streams a SetFile action constructed with the files returned by the server',
        function(done) {
            const retval = AsyncAction.LoadFiles()();

            function handler(val) {
                //ensure stream value was right
                expect(val[0]).toBe('SetFiles');
                expect(val[1]).toBe(files);

                retval.offValue(handler);
                done();
            }

            retval.onValue(handler);

            //trigger async response
            fetchPool.plug(Kefir.later(10, files));
        }
    );

    it('streams a SetErrorMessage action when there is an error', function(done) {
        const retval = AsyncAction.LoadFiles()();

        function handler(val) {
            //ensure stream value was right
            expect(val[0]).toBe('SetErrorMessage');
            expect(val[1]).toBe('Server Error');

            retval.offValue(handler);
            done();
        }

        retval.onValue(handler);

        //trigger async response
        fetchPool.plug(Kefir.later(10, "Server Error").flatMap(Kefir.constantError));
    });
});

describe('AddFile', function() {
    const model = Model({
            uploadModel: UploadModel({
                title: 'test title',
                description: 'desc'
            })
        }),
        serverResponse = {
            title: 'test title',
            description: 'desc',
            filename: 'f1.txt',
            mediaType: 'text/plain',
            href: 'http://localhost/asdf',
            creationDate: '2000-10-31T23:55:99Z'
        };

    it('posts form-data to the backend URI', function(done) {
        const file = new Blob(['test blob']), //Files can't be constructed
                                              //directly but this is close enough
            retval = AsyncAction.AddFile(file)(model);

        function handler() {
            expect(fetchStream.mock.calls[0][0]).toBe(process.env.REACT_APP_BACKEND_URI);
            expect(fetchStream.mock.calls[0][1]).toBe('POST');

            const formData = fetchStream.mock.calls[0][2];
            expect(formData).toBeInstanceOf(FormData);
            expect(formData.get('title')).toBe('test title');
            expect(formData.get('description')).toBe('desc');

            expect(fetchStream.mock.calls[0][3]).toBeUndefined();

            const reader = new FileReader();
            reader.addEventListener("loadend", function() {
                expect(reader.result).toBe('test blob');

                retval.offValue(handler);
                done();
            });

            reader.readAsText(formData.get('file'));
        }

        retval.onValue(handler);

        //trigger async response
        fetchPool.plug(Kefir.later(10, serverResponse));
    });

    it('streams an AddFile action constructed with the file returned by the server',
        function(done) {
            const file = new Blob(),
                retval = AsyncAction.AddFile(file)(model);

            function handler(val) {
                //ensure stream value was right
                expect(val[0]).toBe('AddFile');
                expect(val[1]).toBe(serverResponse);

                retval.offValue(handler);
                done();
            }

            retval.onValue(handler);

            //trigger async response
            fetchPool.plug(Kefir.later(10, serverResponse));
        }
    );

    it('streams a SetErrorMessage action when there is an error', function(done) {
        const file = new Blob(),
            retval = AsyncAction.AddFile(file)(model);

        function handler(val) {
            //ensure stream value was right
            expect(val[0]).toBe('SetErrorMessage');
            expect(val[1]).toBe('Server Error 2');

            retval.offValue(handler);
            done();
        }

        retval.onValue(handler);

        //trigger async response
        fetchPool.plug(Kefir.later(10, "Server Error 2").flatMap(Kefir.constantError));
    });
});
