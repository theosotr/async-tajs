function Stats(
    dev,
    mode,
    nlink,
    uid,
    gid,
    rdev,
    blksize,
    ino,
    size,
    blocks,
    atim_msec,
    mtim_msec,
    ctim_msec,
    birthtim_msec,
    atime,
    mtime,
    ctime,
    birthtime

) {
    this.dev = dev;
    this.mode = mode;
    this.nlink = nlink;
    this.uid = uid;
    this.gid = gid;
    this.rdev = rdev;
    this.blksize = blksize;
    this.ino = ino;
    this.size = size;
    this.blocks = blocks;
    this.atimeMs = atim_msec;
    this.mtimeMs = mtim_msec;
    this.ctimeMs = ctim_msec;
    this.birthtimeMs = birthtim_msec;
    this.atime = atime;
    this.mtime = mtime;
    this.ctime = ctime;
    this.birthtime = birthtime;
}

Stats.prototype.isDirectory = function () {
    return TAJS_make('AnyBool');
};

Stats.prototype.isSymbolicLink = function () {
    return TAJS_make('AnyBool');
};


function access(filename, mode, callback) {
    TAJS_makeContextSensitive(access, 2);
    var err = TAJS_join(TAJS_make('Undef'), TAJS_makeGenericError());
    TAJS_addAsyncIOCallback(callback, err);
}


function close(fd, callback) {
    TAJS_makeContextSensitive(close, 1);
    var err = TAJS_join(TAJS_make('Undef'), TAJS_makeGenericError());
    TAJS_addAsyncIOCallback(callback, err);
}


function open(filename, flags, mode, callback) {
    TAJS_makeContextSensitive(open, 3);
    var err = TAJS_join(TAJS_make('Undef'), TAJS_makeGenericError());
    var fd = TAJS_join(TAJS_make('Undef'), TAJS_make('AnyNum'));
    TAJS_addAsyncIOCallback(callback, err, fd);
}


function readdir(path, options, callback) {
    TAJS_makeContextSensitive(readdir, 2);
    var err = TAJS_join(TAJS_make('Undef'), TAJS_makeGenericError());
    var data = TAJS_join(TAJS_make('Undef'), [], [TAJS_make('AnyStr')]);
    TAJS_addAsyncIOCallback(callback, err, data);
}


function readFile(filename, options, callback) {
    TAJS_makeContextSensitive(readFile, 2);
    var err = TAJS_join(TAJS_make('Undef'), TAJS_makeGenericError());
    var data = TAJS_join(TAJS_make('Undef'), TAJS_make('AnyStr'));
    TAJS_addAsyncIOCallback(callback, err, data);
}


function realPath(filename, options, callback) {
    TAJS_makeContextSensitive(realPath, 2);
    var err = TAJS_join(TAJS_make('Undef'), TAJS_makeGenericError());
    var data = TAJS_join(TAJS_make('Undef'), TAJS_make('AnyStr'));
    TAJS_addAsyncIOCallback(callback, err, data);
}


function stat(path, options, callback) {
    TAJS_makeContextSensitive(stat, 2);
    var err = TAJS_join(TAJS_make('Undef'), TAJS_makeGenericError());
    var stats = new Stats(
        TAJS_make('AnyNum'),
        TAJS_make('AnyNum'),
        TAJS_make('AnyNum'),
        TAJS_make('AnyNum'),
        TAJS_make('AnyNum'),
        TAJS_make('AnyNum'),
        TAJS_make('AnyNum'),
        TAJS_make('AnyNum'),
        TAJS_make('AnyNum'),
        TAJS_make('AnyNum'),
        TAJS_make('AnyNum'),
        TAJS_make('AnyNum'),
        TAJS_make('AnyNum'),
        TAJS_make('AnyNum'),
        TAJS_make('AnyStr'),
        TAJS_make('AnyStr'),
        TAJS_make('AnyStr'),
        TAJS_make('AnyStr'));
    var data = TAJS_join(TAJS_make('Undef'), stats);
    TAJS_addAsyncIOCallback(callback, err, data);
}


function watch(filename, options, callback) {
    TAJS_makeContextSensitive(watch, 2);
    var eventType = TAJS_make('AnyStr');
    var file = TAJS_make('AnyStr');
    TAJS_addAsyncIOCallback(callback, eventType, file);
}


function watchFile(filename, options, callback) {
    TAJS_makeContextSensitive(watchFile, 2);
    var current = new Stats(
        TAJS_make('AnyNum'),
        TAJS_make('AnyNum'),
        TAJS_make('AnyNum'),
        TAJS_make('AnyNum'),
        TAJS_make('AnyNum'),
        TAJS_make('AnyNum'),
        TAJS_make('AnyNum'),
        TAJS_make('AnyNum'),
        TAJS_make('AnyNum'),
        TAJS_make('AnyNum'),
        TAJS_make('AnyNum'),
        TAJS_make('AnyNum'),
        TAJS_make('AnyNum'),
        TAJS_make('AnyNum'),
        TAJS_make('AnyStr'),
        TAJS_make('AnyStr'),
        TAJS_make('AnyStr'),
        TAJS_make('AnyStr'));
    var previous = new Stats(
        TAJS_make('AnyNum'),
        TAJS_make('AnyNum'),
        TAJS_make('AnyNum'),
        TAJS_make('AnyNum'),
        TAJS_make('AnyNum'),
        TAJS_make('AnyNum'),
        TAJS_make('AnyNum'),
        TAJS_make('AnyNum'),
        TAJS_make('AnyNum'),
        TAJS_make('AnyNum'),
        TAJS_make('AnyNum'),
        TAJS_make('AnyNum'),
        TAJS_make('AnyNum'),
        TAJS_make('AnyNum'),
        TAJS_make('AnyStr'),
        TAJS_make('AnyStr'),
        TAJS_make('AnyStr'),
        TAJS_make('AnyStr'));
    TAJS_addAsyncIOCallback(callback, current, previous);
}


function unwatchFile(file) { }


// Synchronous functions
function readdirSync(path, options) {
    return TAJS_join(TAJS_make('Undef'), [], [TAJS_make('AnyStr')]);
}


function realpathSync(filename, options) {
    return TAJS_join(TAJS_make('Undef'), TAJS_make('AnyStr'));
}


function statSync(fd, options) {
    return new Stats(
        TAJS_make('AnyNum'),
        TAJS_make('AnyNum'),
        TAJS_make('AnyNum'),
        TAJS_make('AnyNum'),
        TAJS_make('AnyNum'),
        TAJS_make('AnyNum'),
        TAJS_make('AnyNum'),
        TAJS_make('AnyNum'),
        TAJS_make('AnyNum'),
        TAJS_make('AnyNum'),
        TAJS_make('AnyNum'),
        TAJS_make('AnyNum'),
        TAJS_make('AnyNum'),
        TAJS_make('AnyNum'),
        TAJS_make('AnyStr'),
        TAJS_make('AnyStr'),
        TAJS_make('AnyStr'),
        TAJS_make('AnyStr'));
}


module.exports = {
    access: access,
    close: close,
    lstat: stat,
    open: open,
    readFile : readFile,
    readdir: readdir,
    realpath: realPath,
    stat: stat,
    watch: watch,
    watchFile: watchFile,
    unwatchFile: unwatchFile,

    Stats: Stats,

    lstatSync: statSync,
    readdirSync: readdirSync,
    realpathSync: realpathSync,
    statSync: statSync
};
