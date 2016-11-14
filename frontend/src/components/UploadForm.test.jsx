import React from 'react';
import render from 'react-shallow-renderer';
import * as Immutable from 'immutable';

import mkPool from '../frp/mkPool';

jest.mock('../Action', function() {
    //need to track that the right action constructor was called and what the parameter was, so
    //have the constructors return tuples that name the action and hold its param
    return {
        SetUploadFilename: name => ['SetUploadFilename', name],
        SetUploadTitle: title => ['SetUploadTitle', title],
        SetUploadDescription: desc => ['SetUploadDescription', desc]
    };
});

jest.mock('../AsyncAction', function() {
    return {
        AddFile: file => ['AddFile', file]
    };
});

jest.mock('../Pools', function() {
    return {
        ActionPool: mkPool(),
        AsyncActionPool: mkPool()
    };
});

import { ActionPool, AsyncActionPool } from '../Pools';

import UploadForm from './UploadForm';

import UploadModel from '../model/UploadModel';

const model = UploadModel({
    filename: 'file.txt',
    title: 'My File',
    description: 'this is a file about files'
});

it('renders without crashing with a valid model', function() {
    render(<UploadForm model={model}/>);
});

it('renders a <form> with an id of "upload-form"', function() {
    const tree = render(<UploadForm model={model}/>);

    expect(tree.type).toBe('form');
    expect(tree.props.id).toBe('upload-form');
});

it('sends a SetUploadFilename action to the ActionPool when the file input changes',
    function(done) {
        const fakeEvent = {
                target: { value: 'filename' }
            },
            tree = render(<UploadForm model={model} />),
            fileInput = tree.props.children[0].props.children[1],
            onChange = fileInput.props.onChange;

        function poolListener(val) {
            expect(val.length).toBe(2);
            expect(val[0]).toBe('SetUploadFilename');
            expect(val[1]).toBe('filename');

            ActionPool.stream.offValue(poolListener);
            done();
        }

        ActionPool.stream.onValue(poolListener);

        onChange(fakeEvent);
    }
);

it('sends a SetUploadTitle action to the ActionPool when the title changes',
    function(done) {
        const fakeEvent = {
                target: { value: 'title2' }
            },
            tree = render(<UploadForm model={model} />),
            fileInput = tree.props.children[1].props.children[1],
            onChange = fileInput.props.onChange;

        function poolListener(val) {
            expect(val.length).toBe(2);
            expect(val[0]).toBe('SetUploadTitle');
            expect(val[1]).toBe('title2');

            ActionPool.stream.offValue(poolListener);
            done();
        }

        ActionPool.stream.onValue(poolListener);

        onChange(fakeEvent);
    }
);

it('sends a SetUploadDescription action to the ActionPool when the description changes',
    function(done) {
        const fakeEvent = {
                target: { value: 'descr' }
            },
            tree = render(<UploadForm model={model} />),
            fileInput = tree.props.children[2].props.children[1],
            onChange = fileInput.props.onChange;

        function poolListener(val) {
            expect(val.length).toBe(2);
            expect(val[0]).toBe('SetUploadDescription');
            expect(val[1]).toBe('descr');

            ActionPool.stream.offValue(poolListener);
            done();
        }

        ActionPool.stream.onValue(poolListener);

        onChange(fakeEvent);
    }
);

it('sends a AddFile async action to the AsyncActionPool when the form is submitted',
    function(done) {
        const fakeFile = {},
            mockPreventDefault = jest.fn(),
            fakeEvent = {
                target: [{ files: [fakeFile] }],
                preventDefault: mockPreventDefault
            },
            tree = render(<UploadForm model={model} />),
            onSubmit = tree.props.onSubmit;

        function poolListener(val) {
            expect(val.length).toBe(2);
            expect(val[0]).toBe('AddFile');
            expect(val[1]).toBe(fakeFile);

            expect(mockPreventDefault.mock.calls.length).toBe(1);

            AsyncActionPool.stream.offValue(poolListener);
            done();
        }

        AsyncActionPool.stream.onValue(poolListener);

        onSubmit(fakeEvent);
    }
);

it('disables the submit button if the the filename or title are blank', function() {
    const blankFilenameModel = model.set('filename', ''),
        blankTitleModel = model.set('title', ''),
        spacesTitleModel = model.set('title', ' \t\n   ');

    const normalTree = render(<UploadForm model={model} />),
        blankFilenameTree = render(<UploadForm model={blankFilenameModel} />),
        blankTitleTree = render(<UploadForm model={blankTitleModel} />),
        spacesTitleTree = render(<UploadForm model={spacesTitleModel} />);

    const normalIsDisabled = normalTree.props.children[3].props.disabled,
        blankFilenameIsDisabled = blankFilenameTree.props.children[3].props.disabled,
        blankTitleIsDisabled = blankTitleTree.props.children[3].props.disabled,
        spacesTitleIsDisabled = spacesTitleTree.props.children[3].props.disabled;

    expect(normalIsDisabled).toBe(false);
    expect(blankFilenameIsDisabled).toBe(true);
    expect(blankTitleIsDisabled).toBe(true);
    expect(spacesTitleIsDisabled).toBe(true);
});
