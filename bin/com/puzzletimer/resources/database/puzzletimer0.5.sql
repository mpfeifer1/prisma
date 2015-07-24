BEGIN TRANSACTION;

-- scramblers

INSERT INTO SCRAMBLER VALUES
    (0, 'RUBIKS-CUBE-CLL-TRAINING',             'RUBIKS-CUBE', 'CLL training scrambler',               FALSE),
    (0, 'RUBIKS-CUBE-ELL-TRAINING',             'RUBIKS-CUBE', 'ELL training scrambler',               FALSE),
    (0, 'RUBIKS-CUBE-BLD-SINGLE-STICKER-CYCLE', 'RUBIKS-CUBE', 'BLD - Single sticker cycle scrambler', FALSE);


-- tips

CREATE TABLE TIP(
    TIP_ID VARCHAR(128),
    PUZZLE_ID VARCHAR(128),

    PRIMARY KEY(TIP_ID),
    FOREIGN KEY(PUZZLE_ID) REFERENCES PUZZLE(PUZZLE_ID)
);

INSERT INTO TIP VALUES
    ('RUBIKS-CUBE-OPTIMAL-CROSS',            'RUBIKS-CUBE'),
    ('RUBIKS-CUBE-OPTIMAL-X-CROSS',          'RUBIKS-CUBE'),
    ('RUBIKS-CUBE-3OP-CYCLES',               'RUBIKS-CUBE'),
    ('RUBIKS-CUBE-CLASSIC-POCHMANN-EDGES',   'RUBIKS-CUBE'),
    ('RUBIKS-CUBE-CLASSIC-POCHMANN-CORNERS', 'RUBIKS-CUBE'),
    ('RUBIKS-CUBE-M2-EDGES',                 'RUBIKS-CUBE'),
    ('SQUARE-1-OPTIMAL-CUBE-SHAPE',          'SQUARE-1');


-- category tips

CREATE TABLE CATEGORY_TIPS(
    "ORDER" INTEGER,
    CATEGORY_ID UUID,
    TIP_ID VARCHAR(128),

    PRIMARY KEY(CATEGORY_ID, TIP_ID),
    FOREIGN KEY(CATEGORY_ID) REFERENCES CATEGORY(CATEGORY_ID) ON DELETE CASCADE,
    FOREIGN KEY(TIP_ID) REFERENCES TIP(TIP_ID)
);

INSERT INTO CATEGORY_TIPS VALUES
    -- Rubik's cube
    (0, '90dea358-e525-4b6c-8b2d-abfa61f02a9d', 'RUBIKS-CUBE-OPTIMAL-CROSS'),

    -- Rubik's cube one-handed
    (0, '3282c6bc-3a7b-4b16-aeae-45ae75b17e47', 'RUBIKS-CUBE-OPTIMAL-CROSS'),

    -- Rubik's cube blindfolded
    (0, '953a7701-6235-4f9b-8dd4-fe32055cb652', 'RUBIKS-CUBE-3OP-CYCLES'),
    (1, '953a7701-6235-4f9b-8dd4-fe32055cb652', 'RUBIKS-CUBE-CLASSIC-POCHMANN-CORNERS'),
    (2, '953a7701-6235-4f9b-8dd4-fe32055cb652', 'RUBIKS-CUBE-M2-EDGES'),

    -- Rubik's cube with feet
    (0, '761088a1-64fc-47db-92ea-b6c3b812e6f3', 'RUBIKS-CUBE-OPTIMAL-CROSS'),

    -- Square-1
    (0, '748e6c09-cca5-412a-bd92-cc7febed9adf', 'SQUARE-1-OPTIMAL-CUBE-SHAPE');


CREATE TABLE CATEGORY_TIPS_TEMP(
    "ORDER" INTEGER,
    CATEGORY_ID UUID,
    TIP_ID VARCHAR(128)
);

INSERT INTO CATEGORY_TIPS_TEMP VALUES
    -- Rubik's cube - Easy cross
    (0, 'ccc635c7-9d5d-4920-96ad-4acd87549334', 'RUBIKS-CUBE-OPTIMAL-CROSS'),

    -- 3OP - Corners training
    (0, '5567fdeb-b0a6-43a9-84d6-66f4274c0c7a', 'RUBIKS-CUBE-3OP-CYCLES'),
    (1, '5567fdeb-b0a6-43a9-84d6-66f4274c0c7a', 'RUBIKS-CUBE-CLASSIC-POCHMANN-CORNERS'),

    -- 3OP - Edges training
    (0, 'c6e2d33b-0397-440f-bcb0-72d814d72210', 'RUBIKS-CUBE-3OP-CYCLES'),
    (1, 'c6e2d33b-0397-440f-bcb0-72d814d72210', 'RUBIKS-CUBE-CLASSIC-POCHMANN-EDGES'),
    (2, 'c6e2d33b-0397-440f-bcb0-72d814d72210', 'RUBIKS-CUBE-M2-EDGES');

INSERT INTO CATEGORY_TIPS ("ORDER", CATEGORY_ID, TIP_ID)
    SELECT CTT."ORDER", CTT.CATEGORY_ID, CTT.TIP_ID
    FROM CATEGORY C, CATEGORY_TIPS_TEMP CTT
    WHERE C.CATEGORY_ID = CTT.CATEGORY_ID;

DROP TABLE CATEGORY_TIPS_TEMP;


-- change names of pyraminx faces

UPDATE COLOR
SET FACE_ID = 'TEMP-FACE-D'
WHERE PUZZLE_ID = 'PYRAMINX' AND "ORDER" = 0;

UPDATE COLOR
SET FACE_ID = 'TEMP-FACE-L'
WHERE PUZZLE_ID = 'PYRAMINX' AND "ORDER" = 1;

UPDATE COLOR
SET FACE_ID = 'TEMP-FACE-R'
WHERE PUZZLE_ID = 'PYRAMINX' AND "ORDER" = 2;

UPDATE COLOR
SET FACE_ID = 'TEMP-FACE-F'
WHERE PUZZLE_ID = 'PYRAMINX' AND "ORDER" = 3;


UPDATE COLOR
SET FACE_ID = 'FACE-D', FACE_DESCRIPTION = 'Face D'
WHERE PUZZLE_ID = 'PYRAMINX' AND FACE_ID = 'TEMP-FACE-D';

UPDATE COLOR
SET FACE_ID = 'FACE-L', FACE_DESCRIPTION = 'Face L'
WHERE PUZZLE_ID = 'PYRAMINX' AND FACE_ID = 'TEMP-FACE-L';

UPDATE COLOR
SET FACE_ID = 'FACE-R', FACE_DESCRIPTION = 'Face R'
WHERE PUZZLE_ID = 'PYRAMINX' AND FACE_ID = 'TEMP-FACE-R';

UPDATE COLOR
SET FACE_ID = 'FACE-F', FACE_DESCRIPTION = 'Face F'
WHERE PUZZLE_ID = 'PYRAMINX' AND FACE_ID = 'TEMP-FACE-F';


-- version

UPDATE CONFIGURATION SET VALUE = '0.5' WHERE KEY = 'VERSION';

COMMIT;

