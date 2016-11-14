import React from 'react';

//use react-test-renderer because this component uses a private internal component
//for the rows which is hard to introspect using the shallow renderer
import renderer from 'react-test-renderer';
import * as Immutable from 'immutable';

import FileList from './FileList';
import FileMetadata from '../model/FileMetadata';

const files = Immutable.List.of(
    FileMetadata({
        title: 'file 1',
        href: 'http://localhost/f1',
        description: 'desc1',
        mediaType: 'text/csv',
        creationDate: new Date(631152000000) //year 1990 UTC
    }),
    FileMetadata({
        title: 'file 2',
        href: 'http://localhost/f2',
        description: 'desc2',
        mediaType: 'image/png',
        creationDate: new Date(946684800000) //year 2000
    })
);

it('renders without crashing', function() {
    renderer.create(<FileList files={files} />).toJSON();
});

it('renders a table with an id of "file-list"', function() {
    const tree = renderer.create(<FileList files={files} />).toJSON();

    expect(tree.props.id).toBe('file-list');
});

it('renders a thead with a th for each column', function() {
    const tree = renderer.create(<FileList files={files} />).toJSON(),
        thead = tree.children[0];

    expect(thead.type).toBe('thead');
    expect(thead.children[0].children.length).toBe(3);
    expect(thead.children[0].children[0].type).toBe('th');
});

it('renders a row for each file in the list', function() {
    const tree = renderer.create(<FileList files={files} />).toJSON(),
        tbody = tree.children[1];

    expect(tbody.type).toBe('tbody');
    expect(tbody.children.length).toBe(2);
});

it('renders three columns for each row', function() {
    const tree = renderer.create(<FileList files={files} />).toJSON(),
        row = tree.children[1].children[0];

    expect(row.type).toBe('tr');
    expect(row.children.length).toBe(3);
});

it('renders a link of the files title in the first column', function() {
    const tree = renderer.create(<FileList files={files} />).toJSON(),
        row = tree.children[1].children[0],
        td = row.children[0],
        a = td.children[0];

    expect(td.type).toBe('td');
    expect(a.type).toBe('a');
    expect(a.props.href).toBe('http://localhost/f1');
    expect(a.props.type).toBe('text/csv');
    expect(a.children[0]).toBe('file 1');
});

it('renders the description in the second column', function() {
    const tree = renderer.create(<FileList files={files} />).toJSON(),
        row = tree.children[1].children[0],
        td = row.children[1];

    expect(td.children[0]).toBe('desc1');
});

it('renders the creation date in the third column', function() {
    const tree = renderer.create(<FileList files={files} />).toJSON(),
        row = tree.children[1].children[0],
        td = row.children[2];

    expect(td.children[0]).toBe('01/01/90 00:00:00');
});
