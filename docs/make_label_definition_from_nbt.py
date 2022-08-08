import argparse
from distutils.util import strtobool
# Requires Python-NBT
#   pip install Python-NBT
import python_nbt.nbt as nbt
import json

max_structure_size = 32

def main():
    args = get_args()
    nbt = open_nbt(args.nbt_file)
    block_structure = count_blocks(nbt)
    output_block_list(block_structure, args.ignore_structure_void, args.escape_quotes, args.block_list)

def get_args():
    parser = argparse.ArgumentParser()
    parser.add_argument('nbt_file', help='Path to NBT file')
    parser.add_argument('--block_list', '-l', help='Boolean whether to output a block list of the structure', default='True', type=strtobool)
    parser.add_argument('--escape_quotes', '-e', help='Boolean whether to escape double quotes', default='False', type=strtobool)
    parser.add_argument('--ignore_structure_void', '--iv', help='Boolean whether to ignore Structure Voids', default='True', type=strtobool)
    args = parser.parse_args()
    return args

def open_nbt(nbt_file):
    return nbt.read_from_nbt_file(nbt_file)

def count_blocks(nbt_file):
    palette = []
    for block_type in nbt_file["palette"].value:
        palette.append(block_type["Name"].value)

    block_list = {}
    block_placement = [None] * (max_structure_size * max_structure_size * max_structure_size)
    for block in nbt_file["blocks"]:
        palette_index = block["state"].value
        block_list[palette[palette_index]] = block_list.get(palette[palette_index], 0) + 1
        pos = block["pos"].value
        addr = (pos[0].value * max_structure_size + pos[1].value) * max_structure_size + pos[2].value
        block_placement[addr] = palette_index
    return (block_list, block_placement, palette)

def output_block_list(block_structure, ignore_structure_void, escape_quotes, output_block_list):
    block_list = block_structure[0]
    block_placement = block_structure[1]
    palette = block_structure[2]

    block_array = []
    empty_xz_count = 0

    for x in range(max_structure_size):
        for z in range(max_structure_size):
            empty_y_count = 0
            y_array = []

            for y in range(max_structure_size):
                addr = (x * max_structure_size + y) * max_structure_size + z
                block_number = block_placement[addr]
                block_name = 'minecraft:air'

                if block_number is None:
                    empty_y_count += 1
                else:
                    block_name = palette[block_number]

                    if block_name == 'minecraft:air' or (ignore_structure_void and block_name == 'minecraft:structure_void'):
                        empty_y_count += 1
                    else:
                        # Block exists
                        if empty_y_count > 0:
                            y_array.append(empty_y_count)

                        empty_y_count = 0
                        y_array.append(str(block_number))

            if empty_y_count >= max_structure_size:
                # Y array is empty
                empty_xz_count += 1
            else:
                # Blocks exist in Y array
                if empty_xz_count > 0:
                    block_array.append(empty_xz_count)

                empty_xz_count = 0
                block_array.append(y_array)

    json_str = json.dumps({"labels": block_array})

    if escape_quotes:
        json_str = json_str.replace('"', '\\"')

    print("\tdefinitionInJson = '" + json_str + "'")

    if output_block_list:
        print()

        block_index = 0

        for k, v in block_list.items():
            print(str(block_index) + "," + k + "," + str(v))
            block_index += 1

if __name__ == "__main__":
    main()
